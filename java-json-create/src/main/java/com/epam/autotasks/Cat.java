package com.epam.autotasks;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Cat extends Animal {

    private Breed breed;

    public enum Breed {

        BRITISH(0),
        MAINE_COON(1),
        MUNCHKIN(2),
        PERSIAN(3),
        SIBERIAN(4);

        final int code;

        Breed(int code) {
            this.code = code;
        }

        public static Breed getBreedByCode(int code) {
            for (Breed breed : values()) {
                if (breed.code == code) {
                    return breed;
                }
            }
            return null;
        }
    }
}
