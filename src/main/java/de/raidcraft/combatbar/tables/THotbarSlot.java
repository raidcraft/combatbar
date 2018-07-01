package de.raidcraft.combatbar.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rc_hotbars_slots")
@Getter
@Setter
public class THotbarSlot {

    @Id
    private int id;

    @Column(length = 128)
    private String name;

    @Column(length = 128)
    private String item;

    private int position = -1;

    @ManyToOne
    private THotbar hotbar;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "slot_id")
    private List<THotbarSlotData> data = new ArrayList<>();
}
