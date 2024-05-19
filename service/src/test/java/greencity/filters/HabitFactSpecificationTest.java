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
        SearchCriteria searchCriteria = new SearchCriteria("id", "=", "1");
        List<SearchCriteria> criteriaList = List.of(searchCriteria);
        HabitFactSpecification specification = new HabitFactSpecification(criteriaList);

        Path<Long> idPath = mock(Path.class);

        when(root.get(HabitFact_.id)).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, "1")).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(predicate, "Predicate should not be null");
        verify(criteriaBuilder).equal(idPath, "1");
    }

    @Test
    void toPredicate_HabitIdCriteria_GeneratesCorrectPredicate() {
        SearchCriteria searchCriteria = new SearchCriteria("habitId", "=", "1");
        List<SearchCriteria> criteriaList = List.of(searchCriteria);
        HabitFactSpecification specification = new HabitFactSpecification(criteriaList);

        Join<HabitFact, Habit> habitJoin = mock(Join.class);
        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(criteriaBuilder.equal(habitJoin.get(Habit_.id), "1")).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).equal(habitJoin.get(Habit_.id), "1");
    }

    @Test
    void toPredicate_ContentCriteria_GeneratesCorrectPredicate() {
        SearchCriteria searchCriteria = new SearchCriteria("content", "=", "test");
        List<SearchCriteria> criteriaList = List.of(searchCriteria);
        HabitFactSpecification specification = new HabitFactSpecification(criteriaList);

        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);

        Predicate contentPredicate = mock(Predicate.class);
        when(criteriaBuilder.like(habitFactTranslationRoot.get(HabitFactTranslation_.content), "%test%")).thenReturn(contentPredicate);

        Path<Long> habitFactIdPath = mock(Path.class);
        Path<Long> translationHabitFactIdPath = mock(Path.class);
        when(root.get(HabitFact_.id)).thenReturn(habitFactIdPath);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact).get(HabitFact_.id)).thenReturn(translationHabitFactIdPath);

        Predicate idPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(translationHabitFactIdPath, habitFactIdPath)).thenReturn(idPredicate);

        Predicate conjunctionPredicate = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunctionPredicate);
        when(criteriaBuilder.and(conjunctionPredicate, contentPredicate, idPredicate)).thenReturn(mock(Predicate.class));

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).like(habitFactTranslationRoot.get(HabitFactTranslation_.content), "%test%");
        verify(criteriaBuilder).equal(translationHabitFactIdPath, habitFactIdPath);
    }
}