package ee.ria.riha.storage.client;

import ee.ria.riha.storage.util.FilterRequest;
import ee.ria.riha.storage.util.PageRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * @author Valentin Suhnjov
 */
@RunWith(MockitoJUnitRunner.class)
public class StorageClientTest {

    private static final String URL = "http://storage/api";

    @Spy
    private RestTemplate restTemplate = mock(RestTemplate.class);

    private StorageClient storageClient = new StorageClient(restTemplate, URL);

    @Before
    public void setUp() throws Exception {
        InfoSystemList response = new InfoSystemList();
        response.add("{\n" +
                             "  \"uri\": \"urn:fdc:riha.eesti.ee:2016:infosystem:350811-test\",\n" +
                             "  \"name\": \"LOADTEST\",\n" +
                             "  \"owner\": \"70001484\"\n" +
                             "}");
        response.add("{\n" +
                             "  \"uri\": \"urn:fdc:riha.eesti.ee:2016:classifier:436900\",\n" +
                             "  \"name\": \"Testklassifikaator\",\n" +
                             "  \"owner\": \"21304\"\n" +
                             "}");

        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);
    }

    @Test
    public void dividesResponseArrayToListOfInfoSystemJSONs() throws Exception {
        InfoSystemList infoSystems = storageClient.find("path", InfoSystemList.class);

        assertThat(infoSystems, hasSize(2));
        assertThat(infoSystems.get(0), containsString("urn:fdc:riha.eesti.ee:2016:infosystem:350811-test"));
        assertThat(infoSystems.get(1), containsString("urn:fdc:riha.eesti.ee:2016:classifier:436900"));
    }

    @Test
    public void includesPagingAndFilteringParametersDuringSearch() {
        storageClient.find("path", new PageRequest(5, 3),
                           new FilterRequest("name,ilike,TestSystem", "-modification_date", "owner,name"),
                           InfoSystemList.class);

        verify(restTemplate).getForObject(
                argThat(allOf(
                        containsString("limit=3"),
                        containsString("offset=15"),
                        containsString("filter=name,ilike,TestSystem"),
                        containsString("sort=-modification_date"),
                        containsString("fields=owner,name")
                )),
                anyObject(),
                (Object) anyVararg());
    }

    private static class InfoSystemList extends ArrayList<String> {
    }
}