package io.improbable.keanu.distributions.continuous;

import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.dbl.KeanuRandom;

public class Exponential {

    private Exponential() {
    }

    public static DoubleTensor sample(int[] shape, DoubleTensor location, DoubleTensor lambda, KeanuRandom random) {
        return location.minus(random.nextDouble(shape).logInPlace().timesInPlace(lambda));
    }

    public static DoubleTensor logPdf(DoubleTensor location, DoubleTensor lambda, DoubleTensor x) {
        final DoubleTensor negXMinusADivB = x.minus(location).unaryMinusInPlace().divInPlace(lambda);
        final DoubleTensor negXMinusADivBMinusLogB = negXMinusADivB.minusInPlace(lambda.log());
        return negXMinusADivBMinusLogB.setWithMask(x.getLessThanMask(location), Double.NEGATIVE_INFINITY);
    }

    public static DiffLogP dlnPdf(DoubleTensor location, DoubleTensor lambda, DoubleTensor x) {
        final DoubleTensor dLogPdlocation = lambda.reciprocal();
        final DoubleTensor dLogPdlambda = x.minus(location).minusInPlace(lambda).divInPlace(lambda.pow(2));
        final DoubleTensor dLogPdx = dLogPdlocation.unaryMinus();

        return new DiffLogP(dLogPdlocation, dLogPdlambda, dLogPdx);
    }

    public static class DiffLogP {
        public final DoubleTensor dLogPdlocation;
        public final DoubleTensor dLogPdlambda;
        public final DoubleTensor dLogPdx;

        public DiffLogP(DoubleTensor dLogPdlocation, DoubleTensor dLogPdlambda, DoubleTensor dLogPdx) {
            this.dLogPdlocation = dLogPdlocation;
            this.dLogPdlambda = dLogPdlambda;
            this.dLogPdx = dLogPdx;
        }
    }

}
