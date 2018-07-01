package de.raidcraft.combatbar.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rc_hotbars")
@Getter
@Setter
public class THotbar {

    @Id
    private int id;

    @Column(length = 128)
    private String name;

    @Column(length = 128)
    private String displayName;

    @ManyToOne
    private THotbarHolder holder;

    private int position = 0;

    private boolean active = false;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hotbar_id")
    private List<THotbarSlot> hotbarSlots = new ArrayList<>();
}
