package io.improbable.keanu.algorithms.tensormcmc;

import io.improbable.keanu.network.BayesNetTensorAsContinuous;
import io.improbable.keanu.vertices.dbltensor.DoubleTensor;
import io.improbable.keanu.vertices.dbltensor.KeanuRandom;
import io.improbable.keanu.vertices.dbltensor.probabilistic.TensorGaussianVertex;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TensorMCMCTestDistributions {

    public static BayesNetTensorAsContinuous createSimpleGaussian(double mu, double sigma, KeanuRandom random) {
        TensorGaussianVertex A = new TensorGaussianVertex(new int[]{2, 1}, mu, sigma);
        A.setAndCascade(A.sample(random));
        return new BayesNetTensorAsContinuous(A.getConnectedGraph());
    }

    public static void samplesMatchSimpleGaussian(double mu, double sigma, List<DoubleTensor> samples) {

        int[] shape = samples.get(0).getShape();

        DoubleTensor summed = samples.stream()
            .reduce(DoubleTensor.zeros(shape), DoubleTensor::plusInPlace);

        DoubleTensor averages = summed.divInPlace(samples.size());

        DoubleTensor sumDiffSquared = samples.stream()
            .reduce(
                DoubleTensor.zeros(shape),
                (acc, tensor) -> acc.plusInPlace(tensor.minus(averages).powInPlace(2))
            );

        double[] standardDeviations = sumDiffSquared.div(samples.size() - 1).pow(0.5).getFlattenedView().asArray();
        double[] means = averages.getFlattenedView().asArray();

        for (int i = 0; i < means.length; i++) {
            assertEquals(mu, means[i], 0.05);
            assertEquals(sigma, standardDeviations[i], 0.1);
        }
    }

    public static BayesNetTensorAsContinuous createSumOfGaussianDistribution(double mu, double sigma, double observedSum) {

        TensorGaussianVertex A = new TensorGaussianVertex(mu, sigma);
        TensorGaussianVertex B = new TensorGaussianVertex(mu, sigma);

        TensorGaussianVertex C = new TensorGaussianVertex(A.plus(B), 1.0);
        C.observe(observedSum);

        A.setValue(mu);
        B.setAndCascade(mu);

        return new BayesNetTensorAsContinuous(Arrays.asList(A, B, C));
    }

    public static void samplesMatchesSumOfGaussians(double expected, List<DoubleTensor> sampleA, List<DoubleTensor> samplesB) {

        OptionalDouble averagePosteriorA = sampleA.stream()
            .flatMapToDouble(tensor -> Arrays.stream(tensor.getFlattenedView().asArray()))
            .average();

        OptionalDouble averagePosteriorB = samplesB.stream()
            .flatMapToDouble(tensor -> Arrays.stream(tensor.getFlattenedView().asArray()))
            .average();

        assertEquals(expected, averagePosteriorA.getAsDouble() + averagePosteriorB.getAsDouble(), 0.1);
    }

    public static BayesNetTensorAsContinuous create2DDonutDistribution() {
        TensorGaussianVertex A = new TensorGaussianVertex(0, 1);
        TensorGaussianVertex B = new TensorGaussianVertex(0, 1);

        TensorGaussianVertex D = new TensorGaussianVertex((A.multiply(A)).plus(B.multiply(B)), 0.03);
        D.observe(0.5);

        A.setValue(Math.sqrt(0.5));
        B.setAndCascade(0.0);

        return new BayesNetTensorAsContinuous(Arrays.asList(A, B, D));
    }

    public static void samplesMatch2DDonut(List<DoubleTensor> samplesA, List<DoubleTensor> samplesB) {

        boolean topOfDonut, rightOfDonut, bottomOfDonut, leftOfDonut, middleOfDonut;
        topOfDonut = rightOfDonut = bottomOfDonut = leftOfDonut = middleOfDonut = false;

        for (int i = 0; i < samplesA.size(); i++) {
            double sampleFromA = samplesA.get(i).scalar();
            double sampleFromB = samplesB.get(i).scalar();

            if (sampleFromA > -0.2 && sampleFromA < 0.2 && sampleFromB > 0.6 && sampleFromB < 0.8) {
                topOfDonut = true;
            } else if (sampleFromA > 0.6 && sampleFromA < 0.8 && sampleFromB > -0.2 && sampleFromB < 0.2) {
                rightOfDonut = true;
            } else if (sampleFromA > -0.2 && sampleFromA < 0.2 && sampleFromB > -0.8 && sampleFromB < -0.6) {
                bottomOfDonut = true;
            } else if (sampleFromA > -0.8 && sampleFromA < -0.6 && sampleFromB > -0.2 && sampleFromB < 0.2) {
                leftOfDonut = true;
            } else if (sampleFromA > 0.35 && sampleFromA < 0.45 && sampleFromB > 0.35 && sampleFromB < 0.45) {
                middleOfDonut = true;
            }
        }

        assertTrue(topOfDonut && rightOfDonut && bottomOfDonut && leftOfDonut && !middleOfDonut);
    }

}
