package it.niedermann.nextcloud.remote.deck.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface GenericRemoteMapper<D, T> {

    D toDTO(T to);
    T toTO(D dto);

    default List<D> toDTOList(Collection<T> tos) {
        if (tos == null) {
            return Collections.emptyList();
        }
        List<D> result = new ArrayList<>(tos.size());
        for (T t : tos) {
            result.add(toDTO(t));
        }
        return result;
    }

    default List<T> toTOList(Collection<D> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(dtos.size());
        for (D d : dtos) {
            result.add(toTO(d));
        }
        return result;
    }
}
