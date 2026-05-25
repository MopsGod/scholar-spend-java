package scholarspend.util;

import java.util.List;

public class IdGenerator {

    public static int nextId(List<Integer> existingIds) {
        if (existingIds == null || existingIds.isEmpty()) return 1;
        return existingIds.stream().mapToInt(Integer::intValue).max().getAsInt() + 1;
    }
}
