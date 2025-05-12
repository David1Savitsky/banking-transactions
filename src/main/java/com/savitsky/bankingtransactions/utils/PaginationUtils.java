package com.savitsky.bankingtransactions.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PaginationUtils {

    public static <T> void processInBatch(Function<Pageable, ? extends Slice<T>> itemReq, Consumer<List<T>> itemProc, int batchSize) {
        Pageable pageRequest = PageRequest.of(0, batchSize);
        Slice<T> items;
        do {
            items = itemReq.apply(pageRequest);
            List<T> content = items.getContent();
            if (content.isEmpty()) {
                return;
            }
            itemProc.accept(content);
            pageRequest = items.nextPageable();
        } while (items.hasNext());
    }
}
