package ch.dboeckli.springframeworkguru.kbe.inventory.services.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BeerInventoryTest {

    @Test
    void isNew_returnsTrueWhenIdIsNull() {
        BeerInventory inventory = new BeerInventory();
        inventory.setId(null);

        assertTrue(inventory.isNew());
    }

    @Test
    void isNew_returnsFalseWhenIdIsSet() {
        BeerInventory inventory = new BeerInventory();
        inventory.setId(UUID.randomUUID());

        assertFalse(inventory.isNew());
    }

    @Test
    void equals_returnsTrueForSameInstance() {
        BeerInventory inventory = new BeerInventory();

        assertEquals(inventory, inventory);
    }

    @Test
    void equals_returnsFalseForNull() {
        BeerInventory inventory = new BeerInventory();

        assertNotEquals(null, inventory);
    }

    @Test
    void equals_returnsFalseForDifferentClass() {
        BeerInventory inventory = new BeerInventory();

        assertNotEquals("not-a-beer-inventory", inventory);
    }

    @Test
    void equals_returnsTrueWhenIdsMatchAndNotNull() {
        UUID id = UUID.randomUUID();
        BeerInventory left = new BeerInventory();
        left.setId(id);

        BeerInventory right = new BeerInventory();
        right.setId(id);

        assertEquals(left, right);
        assertEquals(right, left);
    }

    @Test
    void equals_returnsFalseWhenIdIsNull() {
        UUID id = UUID.randomUUID();
        BeerInventory left = new BeerInventory();
        left.setId(null);

        BeerInventory right = new BeerInventory();
        right.setId(id);

        assertNotEquals(left, right);
        assertNotEquals(right, left);
    }

    @Test
    void hashCode_isClassBasedForNonProxyInstances() {
        BeerInventory left = new BeerInventory();
        BeerInventory right = new BeerInventory();

        assertEquals(left.hashCode(), right.hashCode());
        assertEquals(BeerInventory.class.hashCode(), left.hashCode());
    }

}