package org.deeplearning4j.graph.models.deepwalk;

import org.deeplearning4j.graph.api.Edge;
import org.deeplearning4j.graph.api.IGraph;
import org.deeplearning4j.graph.data.GraphLoader;
import org.deeplearning4j.graph.graph.Graph;
import org.deeplearning4j.graph.iterator.RandomWalkIterator;
import org.deeplearning4j.graph.iterator.GraphWalkIterator;
import org.deeplearning4j.graph.models.GraphVectors;
import org.deeplearning4j.graph.models.loader.GraphVectorSerializer;
import org.deeplearning4j.graph.vertexfactory.StringVertexFactory;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDeepWalk {

    @Test
    public void testBasic() throws IOException{
        //Very basic test. Load graph, build tree, call fit, make sure it doesn't throw any exceptions

        ClassPathResource cpr = new ClassPathResource("testgraph_7vertices.txt");

        Graph<String,String> graph = GraphLoader.loadUndirectedGraphEdgeListFile(cpr.getFile().getAbsolutePath(), 7, ",");

        int vectorSize = 5;
        int windowSize = 2;

        DeepWalk<String,String> deepWalk = new DeepWalk.Builder<String,String>().learningRate(0.01)
                .vectorSize(vectorSize)
                .windowSize(windowSize)
                .learningRate(0.01)
                .build();
        deepWalk.initialize(graph);

        for( int i=0; i<7; i++ ){
            INDArray vector = deepWalk.getVertexVector(i);
            assertArrayEquals(new int[]{1,vectorSize},vector.shape());
            System.out.println(Arrays.toString(vector.dup().data().asFloat()));
        }

        GraphWalkIterator<String> iter = new RandomWalkIterator<>(graph,8);

        deepWalk.fit(iter);

        for( int t=0; t<5; t++ ) {
            iter.reset();
            deepWalk.fit(iter);
            System.out.println("--------------------");
            for (int i = 0; i < 7; i++) {
                INDArray vector = deepWalk.getVertexVector(i);
                assertArrayEquals(new int[]{1, vectorSize}, vector.shape());
                System.out.println(Arrays.toString(vector.dup().data().asFloat()));
            }
        }
    }

    @Test
    public void testParallel(){

        IGraph<String,String> graph = generateRandomGraph(1000,10);

        int vectorSize = 20;
        int windowSize = 2;

        DeepWalk<String,String> deepWalk = new DeepWalk.Builder<String,String>().learningRate(0.01)
                .vectorSize(vectorSize)
                .windowSize(windowSize)
                .learningRate(0.01)
                .build();
        deepWalk.initialize(graph);



        deepWalk.fit(graph,8);
    }


    private static Graph<String,String> generateRandomGraph(int nVertices, int nEdgesPerVertex){

        Random r = new Random(12345);

        Graph<String,String> graph = new Graph<String, String>(nVertices,new StringVertexFactory());
        for( int i=0; i<nVertices; i++ ){
            for( int j=0; j<nEdgesPerVertex; j++ ){
                int to = r.nextInt(nVertices);
                Edge<String> edge = new Edge<>(i,to,i+"--"+to,false);
                graph.addEdge(edge);
            }
        }
        return graph;
    }


    @Test
    public void testVerticesNearest(){

        int nVertices = 20;
        Graph<String,String> graph = generateRandomGraph(nVertices,8);

        int vectorSize = 5;
        int windowSize = 2;
        DeepWalk<String,String> deepWalk = new DeepWalk.Builder<String,String>().learningRate(0.01)
                .vectorSize(vectorSize)
                .windowSize(windowSize)
                .learningRate(0.01)
                .build();
        deepWalk.initialize(graph);

        deepWalk.fit(graph,10);

        int topN = 5;
        int nearestTo = 4;
        int[] nearest = deepWalk.verticesNearest(nearestTo,topN);
        double[] cosSim = new double[topN];
        double minSimNearest = 1;
        for( int i=0; i<topN; i++ ){
            cosSim[i] = deepWalk.similarity(nearest[i], nearestTo);
            minSimNearest = Double.min(minSimNearest,cosSim[i]);
            if( i > 0 ) assertTrue(cosSim[i] <= cosSim[i-1]);
        }

        for( int i=0; i<nVertices; i++ ){
            if(i == nearestTo) continue;
            boolean skip = false;
            for( int j=0; j<nearest.length; j++ ){
                if(i == nearest[j]){
                    skip = true;
                    continue;
                }
            }
            if(skip) continue;

            double sim = deepWalk.similarity(i,nearestTo);
            System.out.println(i + "\t" + nearestTo + "\t" + sim);
            assertTrue(sim <= minSimNearest);
        }
    }

    @Test
    public void testLoadingSaving() throws IOException{

        String out = System.getProperty("java.io.tmpdir") + "dl4jdwtestout.txt";

        int nVertices = 20;
        Graph<String,String> graph = generateRandomGraph(nVertices,8);

        int vectorSize = 5;
        int windowSize = 2;
        DeepWalk<String,String> deepWalk = new DeepWalk.Builder<String,String>().learningRate(0.01)
                .vectorSize(vectorSize)
                .windowSize(windowSize)
                .learningRate(0.01)
                .build();
        deepWalk.initialize(graph);

        deepWalk.fit(graph, 10);

        GraphVectorSerializer.writeGraphVectors(deepWalk, out);

        GraphVectors<String,String> vectors = (GraphVectors<String,String>)GraphVectorSerializer.loadTxtVectors(new File(out));

        assertEquals(deepWalk.numVertices(),vectors.numVertices());
        assertEquals(deepWalk.getVectorSize(),vectors.getVectorSize());

        for( int i=0; i<nVertices; i++ ){
            INDArray vecDW = deepWalk.getVertexVector(i);
            INDArray vecLoaded = vectors.getVertexVector(i);

            for( int j=0; j<vectorSize; j++ ){
                double d1 = vecDW.getDouble(j);
                double d2 = vecLoaded.getDouble(j);
                double relError = Math.abs(d1-d2) / (Math.abs(d1) + Math.abs(d2));
                assertTrue(relError < 1e-6);
            }
        }

    }
}
