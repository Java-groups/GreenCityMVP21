package greencity.filters;

import greencity.entity.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitFactSpecificationTest {

    @Mock
    private Root<HabitFact> root;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Test
    void toPredicate_IdCriteria_GeneratesCorrectPredicate() {
        SearchCriteria searchCriteria = new SearchCriteria("1", "1", "id");
        List<SearchCriteria> criteriaList = List.of(searchCriteria);
        HabitFactSpecification specification = new HabitFactSpecification(criteriaList);

        Path idPath = mock(Path.class);

        when(root.get("1")).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, "1")).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(predicate, "Predicate should not be null");
        verify(criteriaBuilder).equal(idPath, "1");
    }

    @Test
    void toPredicate_HabitIdCriteria_GeneratesCorrectPredicate() {
        SearchCriteria searchCriteria = new SearchCriteria("1", "1", "habitId");
        List<SearchCriteria> criteriaList = List.of(searchCriteria);
        HabitFactSpecification specification = new HabitFactSpecification(criteriaList);

        Join<HabitFact, Habit> habitJoin = mock(Join.class);
        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(criteriaBuilder.equal(habitJoin.get(Habit_.id), "1")).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).equal(habitJoin.get(Habit_.id), "1");
    }


    @Test
    void toPredicate_ContentCriteria_GeneratesCorrectPredicate() {
        SearchCriteria searchCriteria = new SearchCriteria("test", "1", "content");
        List<SearchCriteria> criteriaList = List.of(searchCriteria);
        HabitFactSpecification specification = new HabitFactSpecification(criteriaList);

        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        Path<Long> translationHabitFactIdPath = mock(Path.class);
        Predicate conjunctionPredicate = mock(Predicate.class);
        Predicate contentPredicate = mock(Predicate.class);
        Path<HabitFact> habitFactPath = mock(Path.class);
        Path<Long> habitFactIdPath = mock(Path.class);
        Predicate idPredicate = mock(Predicate.class);

        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);
        when(criteriaBuilder.like(any(Path.class), eq("%test%"))).thenReturn(contentPredicate);
        when(root.get(HabitFact_.id)).thenReturn(habitFactIdPath);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact)).thenReturn(habitFactPath);
        when(habitFactPath.get(HabitFact_.id)).thenReturn(translationHabitFactIdPath);
        when(criteriaBuilder.equal(translationHabitFactIdPath, habitFactIdPath)).thenReturn(idPredicate);
        when(criteriaBuilder.conjunction()).thenReturn(conjunctionPredicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).like(any(Path.class), eq("%test%"));
        verify(criteriaBuilder).equal(translationHabitFactIdPath, habitFactIdPath);
    }
}