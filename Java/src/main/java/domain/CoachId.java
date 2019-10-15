package domain;

import java.util.Objects;

public class CoachId {

    public final String id;

    public CoachId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachId coachId = (CoachId) o;
        return Objects.equals(id, coachId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
