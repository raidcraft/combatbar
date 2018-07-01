package de.raidcraft.combatbar.tables;

import de.raidcraft.api.config.KeyValueMap;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rc_hotbars_slots_data")
@Getter
@Setter
public class THotbarSlotData implements KeyValueMap {

    @Id
    private int id;

    @ManyToOne
    private THotbarSlot slot;

    private String dataKey;
    private String dataValue;
}
