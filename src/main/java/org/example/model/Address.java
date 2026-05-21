package org.example.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {
    private String city;
    private String street;
    private int buildingNumber;
}
