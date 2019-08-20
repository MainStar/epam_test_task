import javax.jcr.query.QueryResult;
import com.day.cq.wcm.api.Page;
import javax.jcr.Node;

def pageSearchService = getService("epam.core.services.PageSearchService")

QueryResult result = pageSearchService.executeQueryWithKeyword(session, "/content")
String[] pathsArr = pageSearchService.extractPaths(result.getNodes()).toString().trim().split("\n")
List<String> pathsList = Arrays.asList(pathsArr)

for(String path : pathsList){
    Node node = resourceResolver.getResource(path).adaptTo(Node.class);
    Page page = getPage(node.getParent().getPath())
    if(page != null){
        node.setProperty("parentPagePath", page.getPath())
    }
}

if(data.DRY_RUN){
    saveSession()
}

private void saveSession(){
    log.info("Saving session")
    session.save()
}