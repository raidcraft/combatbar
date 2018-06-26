package de.raidcraft.combatbar.tables;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "rc_combatbars")
@Getter
@Setter
public class THotbar {

    @Id
    private int id;

    @NotEmpty
    private String playerId;

    @Column(nullable = false, length = 32)
    @NotEmpty
    private String name;

    @NotNull
    private int hotbarIndex = 0;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hotbar_id")
    private List<THotbarSlot> hotbarSlots;
}
