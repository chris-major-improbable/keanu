package io.improbable.keanu.vertices.dbl.probabilistic;

import io.improbable.keanu.distributions.continuous.ChiSquared;
import io.improbable.keanu.tensor.Tensor;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.intgr.nonprobabilistic.ConstantIntegerVertex;

import java.util.Map;

import static io.improbable.keanu.tensor.TensorShapeValidation.checkTensorsMatchNonScalarShapeOrAreScalar;

public class ChiSquaredVertex extends ProbabilisticDouble {

    private IntegerVertex k;

    /**
     * One k that must match a proposed tensor shape of ChiSquared
     * <p>
     * If all provided parameters are scalar then the proposed shape determines the shape
     *
     * @param tensorShape the desired shape of the vertex
     * @param k           the number of degrees of freedom
     */
    public ChiSquaredVertex(int[] tensorShape, IntegerVertex k) {
        checkTensorsMatchNonScalarShapeOrAreScalar(tensorShape, k.getShape());

        this.k = k;
        setParents(k);
        setValue(DoubleTensor.placeHolder(tensorShape));
    }

    public ChiSquaredVertex(int[] tensorShape, int k) {
        this(tensorShape, new ConstantIntegerVertex(k));
    }

    /**
     * One to one constructor for mapping some shape of k to
     * a matching shaped ChiSquared.
     *
     * @param k the number of degrees of freedom
     */
    public ChiSquaredVertex(IntegerTensor k) {
        this(k.getShape(), new ConstantIntegerVertex(k));
    }

    public ChiSquaredVertex(int k) {
        this(Tensor.SCALAR_SHAPE, new ConstantIntegerVertex(k));
    }

    @Override
    public DoubleTensor sample(KeanuRandom random) {
        return ChiSquared.sample(getShape(), k.getValue(), random);
    }

    @Override
    public double logPdf(DoubleTensor value) {
        return ChiSquared.logPdf(k.getValue(), value).sum();
    }

    @Override
    public Map<Long, DoubleTensor> dLogPdf(DoubleTensor value) {
        throw new UnsupportedOperationException();
    }

}
