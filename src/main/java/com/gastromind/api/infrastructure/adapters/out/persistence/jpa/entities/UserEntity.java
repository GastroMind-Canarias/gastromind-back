package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @ManyToMany
    @JoinTable(name = "user_allergens", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "allergen_id"))
    private Set<AllergenEntity> allergens;

    public UserEntity() {
    }

    public UserEntity(String id) {
        this.id = id;
    }

    public UserEntity(String id, String name, String email, String password, RoleType role, HouseholdEntity household,
            List<TicketEntity> tickets, List<UsualPurchaseEntity> usualPurchases, Set<AllergenEntity> allergens) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.household = household;
        this.tickets = tickets;
        this.usualPurchases = usualPurchases;
        this.allergens = allergens;
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

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
