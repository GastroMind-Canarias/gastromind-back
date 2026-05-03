package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.RoleType;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
/**
 * Entidad JPA que representa a los usuarios de la plataforma.
 */
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private RoleType role;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private HouseholdEntity household;

    @OneToMany(mappedBy = "user")
    private List<TicketEntity> tickets;

    @OneToMany(mappedBy = "user")
    private List<UsualPurchaseEntity> usualPurchases;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_allergens",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_user_allergens_user_allergen",
                    columnNames = {"user_id", "allergen_id"}))
    private Set<AllergenEntity> allergens;

    @ManyToMany
    @JoinTable(name = "user_favorites", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private Set<RecipeEntity> favoriteRecipes;
    /** Constructor vacio requerido por JPA. */

    public UserEntity() {
    }
    /** Constructor auxiliar cuando solo se dispone del identificador. */

    public UserEntity(String id) {
        this.id = id;
    }
    /** Constructor completo de la entidad de usuario. */

    public UserEntity(String id, String name, String email, String password, RoleType role, HouseholdEntity household, List<TicketEntity> tickets, List<UsualPurchaseEntity> usualPurchases, Set<AllergenEntity> allergens, Set<RecipeEntity> favoriteRecipes) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.household = household;
        this.tickets = tickets;
        this.usualPurchases = usualPurchases;
        this.allergens = allergens;
        this.favoriteRecipes = favoriteRecipes;
    }
    /**
     * Devuelve id.
     * @return valor actual.
     */

    public String getId() {
        return id;
    }
    /**
     * Define id.
     * @param id el identificador del recurso
     */

    public void setId(String id) {
        this.id = id;
    }
    /**
     * Devuelve name.
     * @return valor actual.
     */

    public String getName() {
        return name;
    }
    /**
     * Define name.
     * @param name el nombre
     */

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Devuelve email.
     * @return valor actual.
     */

    public String getEmail() {
        return email;
    }
    /**
     * Define email.
     * @param email el correo electronico
     */

    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Devuelve password.
     * @return valor actual.
     */

    public String getPassword() {
        return password;
    }
    /**
     * Define password.
     * @param password la contrasena
     */

    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Devuelve household.
     * @return resultado de la operacion solicitada.
     */

    public HouseholdEntity getHousehold() {
        return household;
    }
    /**
     * Define household.
     * @param household valor a utilizar.
     */

    public void setHousehold(HouseholdEntity household) {
        this.household = household;
    }
    /**
     * Devuelve tickets.
     * @return lista actual.
     */

    public List<TicketEntity> getTickets() {
        return tickets;
    }
    /**
     * Define tickets.
     * @param tickets valor a utilizar.
     */

    public void setTickets(List<TicketEntity> tickets) {
        this.tickets = tickets;
    }
    /**
     * Devuelve usual purchases.
     * @return lista actual.
     */

    public List<UsualPurchaseEntity> getUsualPurchases() {
        return usualPurchases;
    }
    /**
     * Define usual purchases.
     * @param usualPurchases valor a utilizar.
     */

    public void setUsualPurchases(List<UsualPurchaseEntity> usualPurchases) {
        this.usualPurchases = usualPurchases;
    }
    /**
     * Devuelve allergens.
     * @return resultado de la operacion solicitada.
     */

    public Set<AllergenEntity> getAllergens() {
        return allergens;
    }
    /**
     * Define allergens.
     * @param allergens valor a utilizar.
     */

    public void setAllergens(Set<AllergenEntity> allergens) {
        this.allergens = allergens;
    }
    /**
     * Devuelve favorite recipes.
     * @return resultado de la operacion solicitada.
     */

    public Set<RecipeEntity> getFavoriteRecipes() {
        return favoriteRecipes;
    }
    /**
     * Define favorite recipes.
     * @param favoriteRecipes valor a utilizar.
     */

    public void setFavoriteRecipes(Set<RecipeEntity> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }
    /**
     * Devuelve role.
     * @return resultado de la operacion solicitada.
     */

    public RoleType getRole() {
        return role;
    }
    /**
     * Define role.
     * @param role el rol
     */

    public void setRole(RoleType role) {
        this.role = role;
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param o valor a utilizar.
     * @return true si cumple la condicion; false en caso contrario.
     */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(getId(), that.getId());
    }
    /**
     * Calcula el hash de la instancia.
     * @return valor configurado.
     */

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}




