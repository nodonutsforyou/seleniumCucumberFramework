package ryan.air.page.data.dataEntities;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * holder of persons
 * Created by MVostrikov on 21.09.2016.
 */
public class Persons {


    @XStreamImplicit(itemFieldName = "person")
    private List<Person> personsList;


    public List<Person> getPersonList() {
        if (personsList==null) personsList = new ArrayList<>();
        return personsList;
    }
}
