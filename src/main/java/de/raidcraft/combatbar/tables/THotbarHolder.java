package de.raidcraft.combatbar.tables;

import com.avaje.ebean.validation.NotNull;
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

    @NotNull
    @Column(unique = true)
    private UUID playerId;

    @NotNull
    private int activeHotbar = 0;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "holder_id")
    private List<THotbar> hotbars = new ArrayList<>();
}
