package dk.dbc.search.solrdocstore;

import dk.dbc.search.solrdocstore.queue.QueueJob;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AgencyClassifierItemKey implements Serializable {

    private static final long serialVersionUID = -2054293971622143423L;

    private int agencyId;
    private String classifier;
    private String bibliographicRecordId;

    public AgencyClassifierItemKey() {
    }

    public AgencyClassifierItemKey(int agencyId, String classifier, String bibliographicRecordId) {
        this.agencyId = agencyId;
        this.classifier = classifier;
        this.bibliographicRecordId = bibliographicRecordId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + this.agencyId;
        hash = 43 * hash + Objects.hashCode(this.classifier);
        hash = 43 * hash + Objects.hashCode(this.bibliographicRecordId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AgencyClassifierItemKey other = (AgencyClassifierItemKey) obj;
        if (this.agencyId != other.agencyId) {
            return false;
        }
        if (!Objects.equals(this.classifier, other.classifier)) {
            return false;
        }
        if (!Objects.equals(this.bibliographicRecordId, other.bibliographicRecordId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AgencyItemKey{" +
                "agencyId=" + agencyId +
                "classifier=" + classifier +
                ", bibliographicRecordId='" + bibliographicRecordId + '\'' +
                '}';
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getBibliographicRecordId() {
        return bibliographicRecordId;
    }

    public void setBibliographicRecordId(String bibliographicRecordId) {
        this.bibliographicRecordId = bibliographicRecordId;
    }

    public QueueJob toQueueJob(Integer commitWithin) {
        return new QueueJob(agencyId, classifier, bibliographicRecordId, commitWithin);
    }
}