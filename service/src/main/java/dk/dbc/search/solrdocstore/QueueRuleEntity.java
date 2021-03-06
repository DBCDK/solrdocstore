package dk.dbc.search.solrdocstore;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "queueRule")
public class QueueRuleEntity implements Serializable {

    private static final long serialVersionUID = -2773872842011755768L;

    public QueueRuleEntity() {
    }

    public QueueRuleEntity(String queue) {
        this.queue = queue;
    }

    @Id
    private String queue;

    @Override
    public int hashCode() {
        return queue.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final QueueRuleEntity other = (QueueRuleEntity) obj;
        return Objects.equals(this.queue, other.queue);
    }

    @Override
    public String toString() {
        return "QueueRuleEntity{" + "queue=" + queue + '}';
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

}
