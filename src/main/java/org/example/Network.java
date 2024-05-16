package org.example;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Network {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "network")
    private List<EnergyStore> energyStores;

    protected Network() {
    }

    public Network(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
