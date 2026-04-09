package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.RoleType;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
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

    public UserEntity() {
    }

    public UserEntity(String id) {
        this.id = id;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HouseholdEntity getHousehold() {
        return household;
    }

    public void setHousehold(HouseholdEntity household) {
        this.household = household;
    }

    public List<TicketEntity> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketEntity> tickets) {
        this.tickets = tickets;
    }

    public List<UsualPurchaseEntity> getUsualPurchases() {
        return usualPurchases;
    }

    public void setUsualPurchases(List<UsualPurchaseEntity> usualPurchases) {
        this.usualPurchases = usualPurchases;
    }

    public Set<AllergenEntity> getAllergens() {
        return allergens;
    }

    public void setAllergens(Set<AllergenEntity> allergens) {
        this.allergens = allergens;
    }

    public Set<RecipeEntity> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(Set<RecipeEntity> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
