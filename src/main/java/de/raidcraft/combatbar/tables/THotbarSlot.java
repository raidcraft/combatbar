package de.raidcraft.combatbar.tables;

import com.avaje.ebean.validation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rc_combatbar_slots")
@Getter
@Setter
public class THotbarSlot {

    @Id
    private int id;

    @NotNull
    private int slot = 0;

    @ManyToOne
    private THotbar hotbar;
}
