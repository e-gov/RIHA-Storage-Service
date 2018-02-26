package ee.ria.riha.storage.util;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompositeFilterRequest {

    private List<String> filterParameters;
    private List<String> sortParameters;

    public CompositeFilterRequest addFilterParameter(String filterParameter) {
        if (filterParameters == null) {
            filterParameters = Lists.newArrayList();
        }

        filterParameters.add(filterParameter);
        return this;
    }

    public CompositeFilterRequest addSortParameter(String sortParameter) {
        if (sortParameters == null) {
            sortParameters = Lists.newArrayList();
        }

        sortParameters.add(sortParameter);
        return this;
    }

    public CompositeFilterRequest addFilterParameters(List<String> filterParameters) {
        if (this.filterParameters == null) {
            this.filterParameters = Lists.newArrayList();
        }

        this.filterParameters.addAll(filterParameters);
        return this;
    }

    public CompositeFilterRequest addSortParameters(List<String> sortParameters) {
        if (this.sortParameters == null) {
            this.sortParameters = Lists.newArrayList();
        }

        this.sortParameters.addAll(sortParameters);
        return this;
    }
}
