package dk.dbc.search.solrdocstore;

import dk.dbc.vipcore.marshallers.LibraryRule;
import dk.dbc.vipcore.marshallers.LibraryRules;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "OpenAgencyCache")
public class OpenAgencyEntity implements Serializable {

    private static final long serialVersionUID = 2352663426617537636L;
    private static final String SCHOOLLIBRARY = "Skolebibliotek";

    @Id
    private int agencyId;

    @Enumerated(EnumType.STRING)
    private LibraryType libraryType;

    private boolean partOfDanbib;

    private boolean partOfBibDk;

    private boolean authCreateCommonRecord;

    private Timestamp fetched;

    private boolean valid;

    public OpenAgencyEntity() {
    }

    public OpenAgencyEntity(int agencyId, LibraryType libraryType, boolean authCreateCommonRecord, boolean partOfBibDk, boolean partOfDanbib) {
        this.agencyId = agencyId;
        this.libraryType = libraryType;
        this.authCreateCommonRecord = authCreateCommonRecord;
        this.partOfBibDk = partOfBibDk;
        this.partOfDanbib = partOfDanbib;
        this.fetched = Timestamp.from(Instant.now());
        this.valid = true;
    }

    private static boolean getLibraryRuleBoolean(List<LibraryRule> libraryRuleList, String libraryRuleName) {
        return libraryRuleList != null &&
               libraryRuleList.stream()
                       .filter(lr -> lr != null && lr.getName().equals(libraryRuleName))
                       .findAny()
                       .map(LibraryRule::getBool)
                       .orElse(false);
    }

    private LibraryType getLibraryTypeFromLibraryRules(LibraryRules libraryRules) {
        LibraryType agencyType = LibraryType.NonFBS;
        if (getLibraryRuleBoolean(libraryRules.getLibraryRule(), "use_enrichments")) {
            if (SCHOOLLIBRARY.equals(libraryRules.getAgencyType())) {
                agencyType = LibraryType.FBSSchool;
            } else {
                agencyType = LibraryType.FBS;
            }
        }
        return agencyType;
    }

    public OpenAgencyEntity(LibraryRules libraryRules) {
        Integer aId = Integer.valueOf(libraryRules.getAgencyId());
        this.agencyId = aId == null ? -1 : aId;
        this.libraryType = getLibraryTypeFromLibraryRules(libraryRules);
        List<LibraryRule> libraryRuleList = libraryRules.getLibraryRule();
        this.authCreateCommonRecord = getLibraryRuleBoolean(libraryRuleList, "auth_create_common_record");
        this.partOfBibDk = getLibraryRuleBoolean(libraryRuleList, "part_of_bibliotek_dk");
        this.partOfDanbib = getLibraryRuleBoolean(libraryRuleList, "part_of_danbib");
        this.fetched = Timestamp.from(Instant.now());
        this.valid = true;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public LibraryType getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(LibraryType libraryType) {
        this.libraryType = libraryType;
    }

    public boolean getPartOfDanbib() {
        return partOfDanbib;
    }

    public void setPartOfDanbib(boolean partOfDanbib) {
        this.partOfDanbib = partOfDanbib;
    }

    public boolean getPartOfBibDk() {
        return partOfBibDk;
    }

    public void setPartOfBibDk(boolean partOfBibDk) {
        this.partOfBibDk = partOfBibDk;
    }

    public boolean getAuthCreateCommonRecord() {
        return authCreateCommonRecord;
    }

    public void setAuthCreateCommonRecord(boolean authCreateCommonRecord) {
        this.authCreateCommonRecord = authCreateCommonRecord;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Timestamp getFetched() {
        return fetched;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public void setFetched(Timestamp fetched) {
        this.fetched = fetched;
    }

    public void setFetchedNow() {
        this.fetched = Timestamp.from(Instant.now());
    }

    public long getFetchedAgeMs() {
        return Instant.now().toEpochMilli() - fetched.toInstant().toEpochMilli();
    }

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.agencyId;
        hash = 67 * hash + Objects.hashCode(this.libraryType);
        hash = 67 * hash + ( this.authCreateCommonRecord ? 1 : 0 );
        hash = 67 * hash + ( this.partOfBibDk ? 1 : 0 );
        hash = 67 * hash + ( this.partOfDanbib ? 1 : 0 );
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final OpenAgencyEntity other = (OpenAgencyEntity) obj;
        return this.agencyId == other.agencyId &&
               this.authCreateCommonRecord == other.authCreateCommonRecord &&
               this.partOfBibDk == other.partOfBibDk &&
               this.partOfDanbib == other.partOfDanbib &&
               this.libraryType == other.libraryType;
    }

    @Override
    public String toString() {
        return "OpenAgencyEntity{" + "agencyId=" + agencyId + ", libraryType=" + libraryType + ", partOfDanbib=" + partOfDanbib + ", partOfBibDk=" + partOfBibDk + ", authCreateCommonRecord=" + authCreateCommonRecord + ", fetched=" + fetched + ", valid=" + valid + '}';
    }

}
