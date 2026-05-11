# Integración con IA (Gemini)

La aplicación usa la API **Google Gemini** para dos caminos: **sugerencia de recetas** a partir del inventario del hogar y **extracción estructurada de tickets** desde imagen. Ambos pasan por `GeminiGenerateContentClient`, que puede **probar modelos en cascada** si el principal falla con errores recuperables (red, 429, 503, 5xx según la política de reintentos).

Variables relevantes en configuración:

- `GEMINI_API_KEY` / `app.ai.gemini.api-key`
- `app.ai.gemini.model` y `app.ai.gemini.fallback-models`

## Flujo de datos: de la nevera al prompt (recetas)

1. El caso de uso [`SuggestRecipeFromHouseholdUseCase`](../src/main/java/com/gastromind/api/application/usecases/SuggestRecipeFromHouseholdUseCase.java) carga miembros del hogar, neveras, ítems de nevera con productos y cantidades, agrega **stock disponible** por producto, recoge **alergenos** de los usuarios y **electrodomésticos** del hogar.
2. Construye un [`HouseholdRecipeContext`](../src/main/java/com/gastromind/api/domain/models/HouseholdRecipeContext.java) (stock, alergenos, electrodomésticos, raciones).
3. [`GeminiRecipeAdapter`](../src/main/java/com/gastromind/api/infrastructure/adapters/out/ai/GeminiRecipeAdapter.java) serializa el inventario a un bloque JSON (`buildStockBlock`) con `product_id`, `name`, `quantity_available` por línea.
4. Ese bloque se incrusta en el **prompt de texto** junto con reglas de negocio (solo IDs del inventario, límites de cantidad, alergenos, lista permitida de electrodomésticos).

El cuerpo HTTP hacia Gemini incluye `generationConfig` con `temperature: 0.6` y **`responseMimeType: application/json`** para forzar salida JSON legible por código.

## Prompt engineering: ejemplo de contrato de salida (recetas)

El texto del prompt obliga a una única respuesta JSON con claves fijas (título, instrucciones, raciones, tiempo, electrodoméstico, dificultad, ingredientes usados con `product_id` y `quantity_used`). La forma exacta está en `buildPrompt` en `GeminiRecipeAdapter`; un extracto representativo:

```
Eres un chef asistente. Debes responder SOLO con un JSON valido (sin markdown ni texto fuera del JSON) con exactamente estas claves y tipos:
{
  "title": string,
  "instructions": string (pasos numerados o claros),
  "servings": number (entero, raciones),
  "prep_time": number (entero, minutos totales aproximados),
  "appliance_needed": string (uno de: ...),
  "difficulty": string (exactamente uno de: EASY, MEDIUM, HARD),
  "ingredients_used": array de { "product_id": string (uuid del inventario), "quantity_used": number }
}
Inventario del hogar: [...]
Reglas: ...
```

Tras la respuesta, el adaptador parsea el JSON anidado en `candidates[0].content.parts[0].text`, valida ingredientes contra el stock conocido y **capa** cantidades al máximo disponible.

## Flujo de datos: ticket desde imagen

[`GeminiTicketExtractionAdapter`](../src/main/java/com/gastromind/api/infrastructure/adapters/out/ai/GeminiTicketExtractionAdapter.java) construye un cuerpo **multimodal** (texto del sistema + imagen en base64), con un prompt que define el JSON del ticket (`store_name`, `purchase_date`, `total_amount`, `lines` con unidades permitidas `g|kg|ml|l|ud`, etc.). La respuesta se parsea a `ExtractedTicketReceipt`.

## Manejo de errores

| Situación | Comportamiento típico |
| --- | --- |
| Sin API key | `AiRecipeException` / `AiTicketException` con mensaje explícito de configuración faltante. |
| Error HTTP o red al llamar Gemini | `RestClientException` envuelta en `AiRecipeException` o `AiTicketException`. |
| Modelo saturado o error transitorio | `GeminiGenerateContentClient` puede **pasar al siguiente modelo** en la lista configurada. |
| Respuesta sin candidatos o texto vacío | `AiRecipeException` ("Respuesta de Gemini sin candidatos" / vacía). |
| JSON inválido o no parseable | Excepción genérica de interpretación envuelta en `AiRecipeException` / `AiTicketException`. |
| JSON válido pero campos raros (p. ej. electrodoméstico desconocido) | Donde el código lo contempla, valores por defecto o filtrado (ej. enum de electrodoméstico inválido → valor por defecto en recetas). |
| Ingredientes que no cuadran con inventario | Líneas descartadas o cantidades ajustadas al máximo en stock en `parseAndValidateIngredients`. |

Las excepciones de dominio suben hasta la capa REST y el manejador global de errores para devolver respuestas HTTP coherentes al cliente.
