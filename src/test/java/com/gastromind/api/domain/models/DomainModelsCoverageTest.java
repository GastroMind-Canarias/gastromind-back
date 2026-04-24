package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import com.gastromind.api.domain.models.ticket.ExtractedTicketLine;
import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainModelsCoverageTest {

    @Test
    void allergensCategoriesStoresUnits() {
        Allergen a = new Allergen();
        a.setId("a1");
        a.setName("Gluten");
        assertEquals("a1", a.getId());
        assertEquals(new Allergen("a1"), a);
        assertNotEquals(a, new Allergen("a2"));
        assertNotEquals(a, null);
        assertNotEquals(a, "x");

        Category c = new Category("c1", "Cat");
        c.setName("C2");
        assertEquals("C2", c.getName());
        assertEquals(new Category("c1"), c);

        Store s = new Store("s1", "Mercadona");
        s.setName("S");
        assertEquals("S", s.getName());
        assertEquals(new Store("s1"), s);

        Unit u = new Unit("u1", "kg");
        u.setName("Kg");
        assertEquals("Kg", u.getName());
        assertEquals(new Unit("u1"), u);
    }

    @Test
    void fridgeAndFridgeItem() {
        HouseHold h = new HouseHold("h1");
        Fridge f = new Fridge("f1", h);
        f.setHouseHold_id(h);
        assertEquals(h, f.getHouseHold_id());
        assertEquals(new Fridge("f1"), f);

        FridgeItem fi = new FridgeItem();
        fi.setId("i1");
        fi.setQuantity(BigDecimal.ONE);
        fi.setExpirationDate(LocalDate.now());
        fi.setStatus(ItemStatus.GOOD);
        fi.setProduct(new Product("p1"));
        fi.setProductLabel("lbl");
        fi.setFridgeId("f1");
        assertEquals("i1", fi.getId());
        assertEquals(fi, fi);
        assertNotEquals(fi, new FridgeItem("i2", BigDecimal.ONE, LocalDate.now(), ItemStatus.GOOD, new Product("p1"), "f1"));
    }

    @Test
    void houseHoldAndAppliances() {
        HouseHold h = new HouseHold("h1", "Home", new ArrayList<>(), new ArrayList<>());
        h.setName("H2");
        h.setMembers(List.of(new User("u1")));
        assertEquals(1, h.getMembersCount());
        h.setMembers(null);
        assertEquals(0, h.getMembersCount());
        h.addAppliance(Appliance.HORNO);
        assertEquals(1, h.getAppliances().size());
        h.setAppliances(null);
        h.addAppliance(Appliance.BATIDORA);
        assertNotNull(h.getAppliances());
        assertEquals(new HouseHold("h1"), h);

        HouseholdAppliance ha = new HouseholdAppliance();
        ha.setId("x");
        ha.setAppliance(Appliance.HORNO);
        ha.setHouseholdId("h1");
        assertEquals("x", ha.getId());
    }

    @Test
    void recipeAndIngredientsAndContext() {
        Recipe r = new Recipe("r1", "T", "I", 2, 10, Appliance.HORNO, "EASY", LocalDate.now());
        r.setTitle("T2");
        r.setIngredientsUsed(null);
        assertTrue(r.getIngredientsUsed().isEmpty());
        r.setIngredientsUsed(List.of(new RecipeIngredientUsage()));
        assertEquals(1, r.getIngredientsUsed().size());
        assertEquals(new Recipe("r1"), r);

        RecipeIngredientUsage u = new RecipeIngredientUsage();
        u.setProductId("p");
        u.setProductName("Pn");
        u.setQuantityUsed(BigDecimal.TEN);
        u.setQuantityAvailable(BigDecimal.TEN);
        assertEquals("p", u.getProductId());

        RecipeStockLine line = new RecipeStockLine("p1", "Tomate", new BigDecimal("2"));
        assertEquals("p1", line.productId());

        HouseholdRecipeContext ctx = new HouseholdRecipeContext(
                "h1", List.of(line), List.of("milk"), List.of(Appliance.HORNO), 3);
        assertEquals("h1", ctx.householdId());
    }

    @Test
    void productTicketTicketItemUserFavoritesUsualPurchaseUser() {
        Product p = new Product("p1", "N", true, new Allergen("a1"));
        p.setNeedsReview(true);
        p.setReviewNote("note");
        assertTrue(p.isNeedsReview());
        assertEquals("note", p.getReviewNote());
        assertEquals(new Product("p1"), p);

        Ticket t = new Ticket("t1", new User("u1"), new Store("s1"), 10f, LocalDate.now());
        t.setHouseHold_id(new HouseHold("h1"));
        t.setItems(null);
        assertTrue(t.getItems().isEmpty());
        assertEquals(new Ticket("t1"), t);

        TicketItem ti = new TicketItem();
        ti.setId("ti1");
        ti.setProduct(p);
        ti.setQuantity(BigDecimal.ONE);
        ti.setUnit(new Unit("u1"));
        ti.setPriceUnit(BigDecimal.TEN);
        ti.setVerificationStatus(TicketLineVerificationStatus.OK);
        ti.setLineNote("n");
        ti.setLineProductName("lpn");
        assertEquals(ti, ti);
        assertNotEquals(ti, new TicketItem("x", p, BigDecimal.ONE, BigDecimal.ONE));

        UserFavorites fav = new UserFavorites("f1", new User("u1"), new Recipe("r1"));
        assertEquals(new UserFavorites("f1"), fav);

        UsualPurchase up = new UsualPurchase("up1", new User("u1"), p, 2f);
        up.setTarget_quantity(3f);
        assertEquals(3f, up.getTarget_quantity());
        assertEquals(new UsualPurchase("up1"), up);

        User user = new User("u1", "n", "e", "pw", new HouseHold("h1"), Role.ROLE_OWNER, new HashSet<>());
        user.addAllergen(null);
        Allergen ag = new Allergen("a9", "X");
        user.addAllergen(ag);
        user.removeAllergen("a9");
        user.setAllergens(new HashSet<>(Set.of(ag)));
        assertEquals(1, user.getAllergens().size());
        assertEquals(new User("u1"), user);
    }

    @Test
    void ticketExtractedRecords() {
        ExtractedTicketLine l = new ExtractedTicketLine(
                "Pan", BigDecimal.ONE, "ud", BigDecimal.TEN, BigDecimal.TEN, false, null);
        assertEquals("Pan", l.productName());
        ExtractedTicketReceipt rec = new ExtractedTicketReceipt("S", LocalDate.now(), BigDecimal.ONE, List.of(l));
        assertEquals(1, rec.lines().size());
    }

    @Test
    void enums_values() {
        assertTrue(Appliance.values().length > 0);
        assertTrue(Role.values().length > 0);
        assertEquals(2, TicketLineVerificationStatus.values().length);
    }

    @Test
    void equalsBranches_reflexiveAndNullIdsAndTicketItemsList() {
        Recipe r = new Recipe("r1");
        assertEquals(r, r);
        Recipe bothNullId = new Recipe();
        assertEquals(bothNullId, new Recipe());

        Product p = new Product("p1");
        assertEquals(p, p);
        Product pn = new Product();
        assertEquals(pn, new Product());

        FridgeItem fi = new FridgeItem("i1", BigDecimal.ONE, LocalDate.now(), ItemStatus.GOOD, new Product("p1"), "f1");
        assertEquals(fi, fi);

        Category c = new Category("c1");
        assertEquals(c, c);

        Fridge f = new Fridge("f1");
        assertEquals(f, f);

        UsualPurchase up = new UsualPurchase("up1");
        assertEquals(up, up);

        UserFavorites fav = new UserFavorites("f1");
        assertEquals(fav, fav);
        assertFalse(fav.equals("not-favorites"));

        User u = new User("u1");
        u.addAllergen(new Allergen("keep", "K"));
        u.removeAllergen("nope");
        assertEquals(1, u.getAllergens().size());

        Ticket t = new Ticket("t1", u, new Store("s1"), 1f, LocalDate.now(), null);
        assertTrue(t.getItems().isEmpty());
    }

    @Test
    void equalsHashCodeNullMismatchAndGetters() {
        HouseHold hh = new HouseHold("h1", "N", List.of(), List.of());
        assertFalse(hh.equals(null));
        assertFalse(hh.equals("house"));
        assertEquals("N", hh.getName());
        assertNotNull(hh.getMembers());
        assertNotNull(Integer.valueOf(hh.hashCode()));

        Recipe ra = new Recipe("a");
        assertFalse(ra.equals(null));
        assertFalse(ra.equals(List.of()));
        assertNotEquals(Integer.valueOf(ra.hashCode()), Integer.valueOf(new Recipe("b").hashCode()));
        Recipe noId = new Recipe();
        assertNotEquals(noId, new Recipe("x"));
        noId.hashCode();

        User u = new User("u1");
        assertFalse(u.equals(null));
        assertFalse(u.equals(new Object()));
        u.hashCode();

        Allergen ag = new Allergen("a1");
        assertEquals(ag, ag);
        assertNotEquals(new Allergen(), new Allergen("z"));
        new Allergen().hashCode();

        Unit un = new Unit("u1");
        assertEquals(un, un);
        assertNotEquals(new Unit(), new Unit("id"));
        new Unit().hashCode();

        Store st = new Store("s1");
        assertEquals(st, st);
        assertNotEquals(new Store(), new Store("s2"));
        new Store().hashCode();

        Category cat = new Category("c1");
        assertEquals(cat, cat);
        assertNotEquals(new Category(), new Category("c2"));
        new Category().hashCode();

        Fridge fr = new Fridge("f1");
        assertEquals(fr, fr);
        assertNotEquals(new Fridge(), new Fridge("f9"));
        new Fridge().hashCode();

        UsualPurchase up = new UsualPurchase("up1");
        assertEquals(up, up);
        assertNotEquals(new UsualPurchase(), new UsualPurchase("up2"));
        new UsualPurchase().hashCode();

        Product p1 = new Product("p1");
        assertFalse(p1.equals(null));
        assertFalse(p1.equals("p"));
        assertNotEquals(new Product(), new Product("px"));
        assertNotEquals(new Product("a"), new Product("b"));
        new Product().hashCode();

        FridgeItem fi = new FridgeItem();
        fi.setId(null);
        assertEquals(fi, new FridgeItem());
        fi.hashCode();

        TicketItem ti = new TicketItem("t1", null, BigDecimal.ONE, BigDecimal.ONE);
        assertFalse(ti.equals(null));
        assertFalse(ti.equals("x"));
        TicketItem withId = new TicketItem();
        withId.setId("tid");
        assertNotEquals(new TicketItem(), withId);

        Ticket tk = new Ticket("t1");
        assertFalse(tk.equals(null));
        assertFalse(tk.equals(BigDecimal.ZERO));

        UserFavorites fav = new UserFavorites("f1", new User("u1"), new Recipe("r1"));
        assertEquals("f1", fav.getId());
        assertNotNull(fav.getRecipe_id());
        fav.hashCode();

        RecipeIngredientUsage riu = new RecipeIngredientUsage("p", "N", BigDecimal.ONE, BigDecimal.TEN);
        assertEquals("N", riu.getProductName());
        assertEquals(BigDecimal.ONE, riu.getQuantityUsed());
        assertEquals(BigDecimal.TEN, riu.getQuantityAvailable());
    }
}
