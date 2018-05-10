package fdse.microservice.domain;

import javax.validation.Valid;


public class QueryTrainType {
    @Valid
    private String id;      //车型ID，每个车型一个ID，比如某个型号的动车

    public QueryTrainType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
