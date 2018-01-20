/*

        * Copyright 2016 Google Inc. All Rights Reserved.

        *

        * Licensed under the Apache License, Version 2.0 (the "License");

        * you may not use this file except in compliance with the License.

        * You may obtain a copy of the License at

        *

        * http://www.apache.org/licenses/LICENSE-2.0

        *

        * Unless required by applicable law or agreed to in writing, software

        * distributed under the License is distributed on an "AS IS" BASIS,

        * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

        * See the License for the specific language governing permissions and

        * limitations under the License.

        */


        import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

        import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

        import com.google.api.client.http.HttpRequest;

        import com.google.api.client.http.HttpRequestInitializer;

        import com.google.api.client.json.JsonFactory;

        import com.google.api.client.json.jackson2.JacksonFactory;


        import com.google.api.services.language.v1beta1.CloudNaturalLanguage;

        import com.google.api.services.language.v1beta1.CloudNaturalLanguageScopes;

        import com.google.api.services.language.v1beta1.model.AnalyzeEntitiesRequest;

        import com.google.api.services.language.v1beta1.model.AnalyzeEntitiesResponse;

        import com.google.api.services.language.v1beta1.model.AnalyzeSentimentRequest;

        import com.google.api.services.language.v1beta1.model.AnalyzeSentimentResponse;

        import com.google.api.services.language.v1beta1.model.AnalyzeSyntaxRequest;

        import com.google.api.services.language.v1beta1.model.AnalyzeSyntaxResponse;

        import com.google.api.services.language.v1beta1.model.AnnotateTextRequest;

        import com.google.api.services.language.v1beta1.model.AnnotateTextResponse;

        import com.google.api.services.language.v1beta1.model.Document;

        import com.google.api.services.language.v1beta1.model.Entity;

        import com.google.api.services.language.v1beta1.model.EntityMention;

        import com.google.api.services.language.v1beta1.model.Features;

        import com.google.api.services.language.v1beta1.model.Sentiment;

        import com.google.api.services.language.v1beta1.model.Token;



        import java.io.IOException;

        import java.io.PrintStream;

        import java.security.GeneralSecurityException;

        import java.util.ArrayList;
        import java.util.List;

        import java.util.Map;
        import java.util.StringJoiner;
        import java.util.Scanner;

/**

 * A sample application that uses the Natural Language API to perform

 * entity, sentiment and syntax analysis.

 */

@SuppressWarnings("serial")

public class Analyze {

    /**

     * Be sure to specify the name of your application. If the application name is {@code null} or

     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".

     */

    private static final String APPLICATION_NAME = "Google-LanguagAPISample/1.0";



    private static final int MAX_RESULTS = 4;

    static String[][] partsOfSpeech;
    static ArrayList<String> subjects = new ArrayList<String>();
    static ArrayList<String> objects = new ArrayList<String>();
    static String root;

    /**

     * Detects entities,sentiment and syntax in a document using the Natural Language API.

     */

    public ArrayList<String> GoogleNLP(String input) throws IOException, GeneralSecurityException {
        
/*        if (args.length != 2) {

            System.err.println("Usage:");

            System.err.printf(

                    "\tjava %s \"command\" \"text to analyze\"\n",

                    Analyze.class.getCanonicalName());

            System.exit(1);

        }

        String command1 = args[0];
        String command2 = args[1];*/
    	
        String text = input;
        
        /*String ansType = "";
        if (text.toLowerCase().contains("who")){
        	ansType = "WHO";
        }else if(text.toLowerCase().contains("when")){
        	ansType = "WHEN";
        }else if(text.toLowerCase().contains("where")){
        	ansType = "WHERE";
        }else{
        	ansType = "GENERAL";
        }*/
        
        //int textLength = text.length();
        
        //partsOfSpeech = new String[textLength][4];

        ArrayList<String> al_entites = new ArrayList<String>();
        ArrayList<String> tweet_text = new ArrayList<String>();

       //Analyze app = new Analyze(getLanguageService());

        printSyntax(System.out, analyzeSyntax(text));
        String entities = printEntities(System.out, analyzeEntities(text));
        
        System.out.println(entities);
        
        for (int i = 0; i < partsOfSpeech.length; i++){
        	if ( entities.contains(partsOfSpeech[i][0])){
        		partsOfSpeech[i][2] = "Yes";
        	}else{
        		partsOfSpeech[i][2] = "No";
        	}
        }
        
        
        for (int i = 0; i < partsOfSpeech.length; i++){
        	for (int j=0; j < 3; j++){
        		System.out.println(partsOfSpeech[i][j]);
        	}
        }
        
        String POS = "NOUN VERB ADJ NUM";
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < partsOfSpeech.length; i++){
        	if(POS.contains(partsOfSpeech[i][1]) || partsOfSpeech[i][2] == "Yes"){
        		query.append(partsOfSpeech[i][0]);
        		query.append("+");
        	}
        }
        
        System.out.println("query with number:" +query);
        

/*        if (command.equals("entities")) {

            al_entites = printEntities(System.out, analyzeEntities(text));

        } else if (command.equals("sentiment")) {

            printSentiment(System.out, analyzeSentiment(text));

        } else if (command.equals("syntax")) {

            printSyntax(System.out, analyzeSyntax(text));

        }*/

        if(query.length() != 0){
            tweet_text = passQueryParams(query,text,objects,subjects,root);
        }
        return tweet_text;
    }


    public ArrayList<String> passQueryParams(StringBuilder query, String text, ArrayList<String> objects, ArrayList<String> subjects, String root){
    	
        ArrayList<String> tweets = new ArrayList<String>();
        HttpQueryExecution hq = new HttpQueryExecution();
        System.out.println("Firing Query");
        tweets = hq.fireQuery(query,text,objects,subjects,root);
        System.out.println("Tweets: "+ tweets);
        
        return tweets;
    }
    /**

     * Print a list of {@code entities}.

     */

    public static String printEntities(PrintStream out, List<Entity> entities) {
    	
    	String entityString ="";
        
        ArrayList<String> al_entity = new ArrayList<String>();

        if (entities == null || entities.size() == 0) {

            out.println("No entities found.");

            //return al_entity;
            return entityString;

        }

        out.printf("Found %d entit%s.\n", entities.size(), entities.size() == 1 ? "y" : "ies");

        for (Entity entity : entities) {
        	
        	entityString = entity.getName() + " " + entityString;

            al_entity.add(entity.getName());

            out.printf("%s\n", entity.getName());

            out.printf("\tSalience: %.3f\n", entity.getSalience());

            out.printf("\tType: %s\n", entity.getType());

            if (entity.getMetadata() != null) {

                for (Map.Entry<String, String> metadata : entity.getMetadata().entrySet()) {

                    out.printf("\tMetadata: %s = %s\n", metadata.getKey(), metadata.getValue());

                }

            }

            if (entity.getMentions() != null) {

                for (EntityMention mention : entity.getMentions()) {

                    for (Map.Entry<String, Object> mentionSetMember : mention.entrySet()) {

                        out.printf("\tMention: %s = %s\n", mentionSetMember.getKey(), mentionSetMember.getValue());

                    }

                }

            }

        }
        
        //return al_entity;
        System.out.println("entityString is "+ entityString);
        return entityString;
        /*
        System.out.println(query);

        */
    }



    /**

     * Print the Sentiment {@code sentiment}.

     */

    public static void printSentiment(PrintStream out, Sentiment sentiment) {

        if (sentiment == null) {

            out.println("No sentiment found");

            return;

        }

        out.println("Found sentiment.");

        out.printf("\tMagnitude: %.3f\n", sentiment.getMagnitude());

        out.printf("\tScore: %.3f\n", sentiment.getScore());

    }



    public static void printSyntax(PrintStream out, List<Token> tokens) {

        if (tokens == null || tokens.size() == 0) {

            out.println("No syntax found");

            return;

        }

        out.printf("Found %d token%s.\n", tokens.size(), tokens.size() == 1 ? "" : "s");
        
        int i = 0;
        partsOfSpeech = new String[tokens.size()][3];
        String object = "DOBJ IOBJ POBJ";
        String subject = "CSUBJ CSUBJPASS NSUBJ NSUBJPASS NOMCSUBJ NOMCSUBJP";
        
        for (Token token : tokens) {
        	
            partsOfSpeech[i][0] = token.getText().getContent();
            partsOfSpeech[i][1] = token.getPartOfSpeech().getTag();
            System.out.println("Word: "+ partsOfSpeech[i][0] + "Tag: "+partsOfSpeech[i][1]);
            if (object.contains(token.getDependencyEdge().getLabel())){
            	objects.add(partsOfSpeech[i][0]);
            }else if (subject.contains(token.getDependencyEdge().getLabel())){
            	subjects.add(partsOfSpeech[i][0]);
            }else if (token.getDependencyEdge().getLabel() == "ROOT"){
            	root = partsOfSpeech[i][0];
            }
            
            System.out.println("Word: "+ partsOfSpeech[i][0] + "Tag: "+partsOfSpeech[i][1]);
            
            i++;

            /*out.println("TextSpan");

            out.printf("\tText: %s\n", token.getText().getContent());

            out.printf("\tBeginOffset: %d\n", token.getText().getBeginOffset());

            out.printf("Lemma: %s\n", token.getLemma());

            out.printf("PartOfSpeechTag: %s\n", token.getPartOfSpeech().getTag());

            out.printf("\tAspect: %s\n",token.getPartOfSpeech().getAspect());

            out.printf("\tCase: %s\n", token.getPartOfSpeech().getCase());

            out.printf("\tForm: %s\n", token.getPartOfSpeech().getForm());

            out.printf("\tGender: %s\n",token.getPartOfSpeech().getGender());

            out.printf("\tMood: %s\n", token.getPartOfSpeech().getMood());

            out.printf("\tNumber: %s\n", token.getPartOfSpeech().getNumber());

            out.printf("\tPerson: %s\n", token.getPartOfSpeech().getPerson());

            out.printf("\tProper: %s\n", token.getPartOfSpeech().getProper());

            out.printf("\tReciprocity: %s\n", token.getPartOfSpeech().getReciprocity());

            out.printf("\tTense: %s\n", token.getPartOfSpeech().getTense());

            out.printf("\tVoice: %s\n", token.getPartOfSpeech().getVoice());

            out.println("DependencyEdge");

            out.printf("\tHeadTokenIndex: %d\n", token.getDependencyEdge().getHeadTokenIndex());

            out.printf("\tLabel: %s\n", token.getDependencyEdge().getLabel());*/

        }

    }



    /**

     * Connects to the Natural Language API using Application Default Credentials.

     */

    public static CloudNaturalLanguage getLanguageService()

            throws IOException, GeneralSecurityException {

        GoogleCredential credential =

                GoogleCredential.getApplicationDefault().createScoped(CloudNaturalLanguageScopes.all());

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        return new CloudNaturalLanguage.Builder(

                GoogleNetHttpTransport.newTrustedTransport(),

                jsonFactory, new HttpRequestInitializer() {

            @Override

            public void initialize(HttpRequest request) throws IOException {

                credential.initialize(request);

            }

        })

                .setApplicationName(APPLICATION_NAME)

                .build();

    }



    private final CloudNaturalLanguage languageApi;



    /**

     * Constructs a {@link Analyze} which connects to the Cloud Natural Language API.

     */

    public Analyze() {
        
        CloudNaturalLanguage lang = null;
        try {
            lang = getLanguageService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        this.languageApi = lang;
        
    }



    /**

     * Gets {@link Entity}s from the string {@code text}.

     */

    public List<Entity> analyzeEntities(String text) throws IOException {

        AnalyzeEntitiesRequest request =

                new AnalyzeEntitiesRequest()

                        .setDocument(new Document().setContent(text).setType("PLAIN_TEXT"))

                        .setEncodingType("UTF16");

        CloudNaturalLanguage.Documents.AnalyzeEntities analyze =

                languageApi.documents().analyzeEntities(request);



        AnalyzeEntitiesResponse response = analyze.execute();

        return response.getEntities();

    }



    /**

     * Gets {@link Sentiment} from the string {@code text}.

     */

    public Sentiment analyzeSentiment(String text) throws IOException {

        AnalyzeSentimentRequest request =

                new AnalyzeSentimentRequest()

                        .setDocument(new Document().setContent(text).setType("PLAIN_TEXT"));

        CloudNaturalLanguage.Documents.AnalyzeSentiment analyze =

                languageApi.documents().analyzeSentiment(request);



        AnalyzeSentimentResponse response = analyze.execute();

        return response.getDocumentSentiment();

    }



    /**

     * Gets {@link Token}s from the string {@code text}.

     */

    public List<Token> analyzeSyntax(String text) throws IOException {
    	System.out.println("Inside analyze syntax: " +text);

        AnalyzeSyntaxRequest request =

                new AnalyzeSyntaxRequest()

                        .setDocument(new Document().setContent(text).setType("PLAIN_TEXT"))

                        .setEncodingType("UTF16");

        CloudNaturalLanguage.Documents.AnalyzeSyntax analyze =

                languageApi.documents().analyzeSyntax(request);

        AnalyzeSyntaxResponse response = analyze.execute();
        
        System.out.println("Output is: " + response.getTokens().toString());

        return response.getTokens();

    }

}