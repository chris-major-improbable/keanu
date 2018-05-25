package io.improbable.keanu.algorithms.tensormcmc;

import io.improbable.keanu.algorithms.NetworkSamples;
import io.improbable.keanu.algorithms.mcmc.MCMCTestDistributions;
import io.improbable.keanu.algorithms.mcmc.NUTS;
import io.improbable.keanu.algorithms.mcmc.tensor.TensorNUTS;
import io.improbable.keanu.network.BayesNetDoubleAsContinuous;
import io.improbable.keanu.network.BayesNetTensorAsContinuous;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbltensor.DoubleTensor;
import io.improbable.keanu.vertices.dbltensor.KeanuRandom;
import org.junit.Before;
import org.junit.Test;

public class TensorNUTSTest {

    private KeanuRandom random;

    @Before
    public void setup() {
        random = new KeanuRandom(1);
    }

    @Test
    public void samplesGaussian() {
        double mu = 0.0;
        double sigma = 1.0;
        BayesNetTensorAsContinuous simpleGaussian = TensorMCMCTestDistributions.createSimpleGaussian(mu, sigma, random);

        NetworkSamples posteriorSamples = TensorNUTS.getPosteriorSamples(
            simpleGaussian,
            simpleGaussian.getLatentVertices(),
            1000,
            0.25,
            random
        );

        Vertex<DoubleTensor> vertex = simpleGaussian.getContinuousLatentVertices().get(0);

        TensorMCMCTestDistributions.samplesMatchSimpleGaussian(mu, sigma, posteriorSamples.get(vertex).asList());
    }

    @Test
    public void samplesContinuousPrior() {

        BayesNetTensorAsContinuous bayesNet = TensorMCMCTestDistributions.createSumOfGaussianDistribution(20.0, 1.0, 46.);

        NetworkSamples posteriorSamples = TensorNUTS.getPosteriorSamples(
            bayesNet,
            bayesNet.getLatentVertices(),
            2000,
            0.1,
            random
        );

        Vertex<DoubleTensor> A = bayesNet.getContinuousLatentVertices().get(0);
        Vertex<DoubleTensor> B = bayesNet.getContinuousLatentVertices().get(1);

        TensorMCMCTestDistributions.samplesMatchesSumOfGaussians(44.0, posteriorSamples.get(A).asList(), posteriorSamples.get(B).asList());
    }

    @Test
    public void samplesFromDonut() {
        BayesNetTensorAsContinuous donutBayesNet = TensorMCMCTestDistributions.create2DDonutDistribution();

        NetworkSamples samples = TensorNUTS.getPosteriorSamples(
            donutBayesNet,
            donutBayesNet.getLatentVertices(),
            1000,
            0.05,
            random
        );

        Vertex<DoubleTensor> A = donutBayesNet.getContinuousLatentVertices().get(0);
        Vertex<DoubleTensor> B = donutBayesNet.getContinuousLatentVertices().get(1);

        TensorMCMCTestDistributions.samplesMatch2DDonut(samples.get(A).asList(), samples.get(B).asList());
    }
}

