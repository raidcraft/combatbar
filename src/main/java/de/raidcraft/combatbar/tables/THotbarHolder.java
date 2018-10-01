package de.raidcraft.combatbar.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rc_hotbars_holders")
@Getter
@Setter
public class THotbarHolder {

    @Id
    private int id;

    private String player;

    @Column(unique = true)
    private UUID playerId;

    private int activeHotbar = 0;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "holder_id")
    private List<THotbar> hotbars = new ArrayList<>();
}
