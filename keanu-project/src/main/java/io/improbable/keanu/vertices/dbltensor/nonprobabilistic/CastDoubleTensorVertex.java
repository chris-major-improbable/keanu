package io.improbable.keanu.vertices.dbltensor.nonprobabilistic;

import io.improbable.keanu.tensor.NumberTensor;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbltensor.KeanuRandom;
import io.improbable.keanu.vertices.dbltensor.nonprobabilistic.diff.TensorDualNumber;

import java.util.Map;

public class CastDoubleTensorVertex extends NonProbabilisticDoubleTensor {

    private final Vertex<? extends NumberTensor> inputVertex;

    public CastDoubleTensorVertex(Vertex<? extends NumberTensor> inputVertex) {
        this.inputVertex = inputVertex;
        setParents(inputVertex);
    }

    @Override
    public DoubleTensor sample(KeanuRandom random) {
        return inputVertex.sample(random).toDouble();
    }

    @Override
    public DoubleTensor getDerivedValue() {
        return inputVertex.getValue().toDouble();
    }

    @Override
    public TensorDualNumber calculateDualNumber(Map<Vertex, TensorDualNumber> dualNumbers) {
        throw new UnsupportedOperationException("CastDoubleTensorVertex is non-differentiable");
    }
}
