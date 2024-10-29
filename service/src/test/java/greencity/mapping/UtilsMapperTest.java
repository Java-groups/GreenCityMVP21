package greencity.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

class Source {
    private String name;
    private int age;

    public Source(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

class Destination {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

public class UtilsMapperTest {
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
    }

    @Test
    void testMap() {
        Source source = new Source("John", 25);

        Destination destination = UtilsMapper.map(source, Destination.class);

        assertNotNull(destination);
        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getAge(), destination.getAge());
    }

    @Test
    void testMapAllToList() {
        List<Source> sourceList = Arrays.asList(
                new Source("Alice", 30),
                new Source("Bob", 40)
        );

        List<Destination> destinationList = UtilsMapper.mapAllToList(sourceList, Destination.class);

        assertNotNull(destinationList);
        assertEquals(2, destinationList.size());
        assertEquals("Alice", destinationList.get(0).getName());
        assertEquals(30, destinationList.get(0).getAge());
        assertEquals("Bob", destinationList.get(1).getName());
        assertEquals(40, destinationList.get(1).getAge());
    }

    @Test
    void testMapAllToSet() {
        List<Source> sourceList = Arrays.asList(
                new Source("Alice", 30),
                new Source("Bob", 40)
        );

        Set<Destination> destinationSet = UtilsMapper.mapAllToSet(sourceList, Destination.class);

        assertNotNull(destinationSet);
        assertEquals(2, destinationSet.size());

        Destination alice = destinationSet.stream().filter(d -> "Alice".equals(d.getName())).findFirst().orElse(null);
        Destination bob = destinationSet.stream().filter(d -> "Bob".equals(d.getName())).findFirst().orElse(null);
        assertNotNull(alice);
        assertEquals(30, alice.getAge());
        assertNotNull(bob);
        assertEquals(40, bob.getAge());
    }
}

