<%@ page import="test.MongoConnector" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.mongodb.*" %>
<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<section style="text-align: center">
    <p><img src="http://i.imgur.com/Jbcwuec.jpg" style="width:800px;">

    </p>

    <p>on <%= (new Date()).toString() %><br>
        with <%= getServletInfo() %>

    </p>
    <%
        MongoConnector mongoConnector = MongoConnector.getConnectedConnector();
        MongoClient mongoClient = mongoConnector.getMongoClient();
        DB db = mongoConnector.getDb();
    %>
    <p>MongoDB version: <%=mongoClient.getVersion()%>
    </p>
    <%
        out.write("Connected to the mongodb, using '" + db.getName() + "'<br>");

        Set<String> colls = db.getCollectionNames();
    %>
    <p>There are <%=colls.size()%> collection(s).</p>
    <%
        out.write("<ul>");
        for (String s : colls) {
            out.write("<li>" + s + "</li>");
        }
        out.write("</ul>");

        DBCollection dbCollection = db.getCollection("test");

        out.write("<p>Collection contents:</p>");
        DBCursor dbCursor = dbCollection.find();

        for (DBObject o : dbCursor.toArray()) {
            out.write(o.toString() + "<br>");
        }
    %>
    <form method="get" action="/test">
        <input type="submit" name="add" value="Add one record">
        <input type="submit" name="clean" value="Clean the collection">
    </form>
</section>
</body>
</html>