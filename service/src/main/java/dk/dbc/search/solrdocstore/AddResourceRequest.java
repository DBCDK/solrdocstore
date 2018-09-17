package dk.dbc.search.solrdocstore;

public class AddResourceRequest extends BibliographicResourceEntity {

    public AddResourceRequest(){}

    public AddResourceRequest(int agencyId, String field, String bibliographicRecordId, boolean value){
        super(agencyId, bibliographicRecordId, field, value);
    }

    BibliographicResourceEntity asBibliographicResource() {
        return new BibliographicResourceEntity(this.getAgencyId(),
                this.getBibliographicRecordId(), this.getField(), this.getValue());
    }
}
