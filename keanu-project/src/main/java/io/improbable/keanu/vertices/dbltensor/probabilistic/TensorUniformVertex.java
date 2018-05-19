package io.improbable.keanu.vertices.dbltensor.probabilistic;

import io.improbable.keanu.distributions.tensors.continuous.TensorUniform;
import io.improbable.keanu.vertices.dbltensor.DoubleTensor;
import io.improbable.keanu.vertices.dbltensor.DoubleTensorVertex;
import io.improbable.keanu.vertices.dbltensor.KeanuRandom;
import io.improbable.keanu.vertices.dbltensor.Tensor;
import io.improbable.keanu.vertices.dbltensor.nonprobabilistic.ConstantTensorVertex;

import java.util.Map;

import static io.improbable.keanu.vertices.dbltensor.TensorShapeValidation.checkHasSingleNonScalarShapeOrAllScalar;
import static io.improbable.keanu.vertices.dbltensor.TensorShapeValidation.checkTensorsMatchNonScalarShapeOrAreScalar;
import static java.util.Collections.singletonMap;

public class TensorUniformVertex extends ProbabilisticDoubleTensor {

    private final DoubleTensorVertex xMin;
    private final DoubleTensorVertex xMax;

    /**
     * @param shape desired tensor shape
     * @param xMin  inclusive
     * @param xMax  exclusive
     */
    public TensorUniformVertex(int[] shape, DoubleTensorVertex xMin, DoubleTensorVertex xMax) {

        checkTensorsMatchNonScalarShapeOrAreScalar(shape, xMin.getValue(), xMax.getValue());

        this.xMin = xMin;
        this.xMax = xMax;
        setParents(xMin, xMax);
        setValue(DoubleTensor.placeHolder(shape));
    }

    public TensorUniformVertex(DoubleTensorVertex xMin, DoubleTensorVertex xMax) {
        this(checkHasSingleNonScalarShapeOrAllScalar(xMin.getValue(), xMax.getValue()), xMin, xMax);
    }

    public TensorUniformVertex(DoubleTensorVertex xMin, double xMax) {
        this(xMin.getShape(), xMin, new ConstantTensorVertex(xMax));
    }

    public TensorUniformVertex(double xMin, DoubleTensorVertex xMax) {
        this(xMax.getShape(), new ConstantTensorVertex(xMin), xMax);
    }

    public TensorUniformVertex(double xMin, double xMax) {
        this(Tensor.SCALAR_SHAPE, new ConstantTensorVertex(xMin), new ConstantTensorVertex(xMax));
    }

    public DoubleTensorVertex getXMin() {
        return xMin;
    }

    public DoubleTensorVertex getXMax() {
        return xMax;
    }

    @Override
    public double logPdf(DoubleTensor value) {
        return TensorUniform.logPdf(xMin.getValue(), xMax.getValue(), value).sum();
    }

    @Override
    public Map<Long, DoubleTensor> dLogPdf(DoubleTensor value) {

        DoubleTensor dlogPdf = DoubleTensor.zeros(this.xMax.getValue().getShape());
        dlogPdf.applyWhereInPlace(value.getGreaterThanMask(xMax.getValue()), Double.NEGATIVE_INFINITY);
        dlogPdf.applyWhereInPlace(value.getLessThanOrEqualToMask(xMin.getValue()), Double.POSITIVE_INFINITY);

        return singletonMap(getId(), dlogPdf);
    }

    @Override
    public DoubleTensor sample(KeanuRandom random) {
        return TensorUniform.sample(getShape(), xMin.getValue(), xMax.getValue(), random);
    }


}
