package openmap.standard;

import openmap.framework.*;
import openmap.standard.*;

import org.junit.Before;
import org.junit.Test;



public class testAnimal {

    Animal animal;

    @Before
    public void setUp() {
        animal = new AnimalImpl();
    }

    @Test
    public void AnimalShouldHaveWeight10(){
        assert (animal.weight() == 10);
    }

    @Test
    public void AnimalShouldNotHaveWeight15(){
        assert (animal.weight() != 15);
    }

}

