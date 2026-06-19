package it.niedermann.nextcloud.deck.data.local.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface GenericMapper<E, T> {

    E toEntity(T to);
    T toTO(E entity);

    default List<E> toEntityList(Collection<T> tos) {
        if (tos == null) {
            return Collections.emptyList();
        }
        List<E> result = new ArrayList<>(tos.size());
        for (T t : tos) {
            result.add(toEntity(t));
        }
        return result;
    }

    default List<T> toTOList(Collection<E> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(entities.size());
        for (E t : entities) {
            result.add(toTO(t));
        }
        return result;
    }
}
