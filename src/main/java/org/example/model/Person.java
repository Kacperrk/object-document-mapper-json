package org.example.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

// import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {
    private String name;

    // TODO: Przywrócić po rozszerzeniu własnego parsera JSON.
    //
    // private int age;
    // private boolean active;
    // private double averageGrade;
    // private List<String> skills;
    // private Address address;
}
