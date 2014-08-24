package org.deeplearning4j.linalg.api.complex;


import static  org.deeplearning4j.linalg.util.ArrayUtil.calcStrides;
import static org.deeplearning4j.linalg.util.ArrayUtil.calcStridesFortran;
import static  org.deeplearning4j.linalg.util.ArrayUtil.reverseCopy;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import org.deeplearning4j.linalg.api.ndarray.BaseNDArray;
import org.deeplearning4j.linalg.api.ndarray.DimensionSlice;
import org.deeplearning4j.linalg.api.ndarray.INDArray;
import org.deeplearning4j.linalg.api.ndarray.SliceOp;
import org.deeplearning4j.linalg.factory.NDArrayFactory;
import org.deeplearning4j.linalg.factory.NDArrays;
import org.deeplearning4j.linalg.indexing.NDArrayIndex;

import org.deeplearning4j.linalg.ops.TwoArrayOps;
import org.deeplearning4j.linalg.ops.elementwise.AddOp;
import org.deeplearning4j.linalg.ops.elementwise.DivideOp;
import org.deeplearning4j.linalg.ops.elementwise.MultiplyOp;
import org.deeplearning4j.linalg.ops.elementwise.SubtractOp;
import org.deeplearning4j.linalg.ops.reduceops.Ops;
import org.deeplearning4j.linalg.ops.transforms.Transforms;
import org.deeplearning4j.linalg.util.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * ComplexNDArray for complex numbers.
 *
 *
 * Note that the indexing scheme for a complex ndarray is 2 * length
 * not length.
 *
 * The reason for this is the fact that imaginary components have
 * to be stored alongside realComponent components.
 *
 * @author Adam Gibson
 */
public abstract class BaseComplexNDArray extends BaseNDArray implements IComplexNDArray {


    public BaseComplexNDArray(double[][] data) {
        super(data);
    }

    /**
     * Create this ndarray with the given data and shape and 0 offset
     *
     * @param data     the data to use
     * @param shape    the shape of the ndarray
     * @param ordering
     */
    public BaseComplexNDArray(double[] data, int[] shape, char ordering) {
        super(data, shape, ordering);
    }

    public BaseComplexNDArray(int[] shape, int offset, char ordering) {
        super(shape, offset, ordering);
    }

    public BaseComplexNDArray(int[] shape) {
        super(shape);
    }



    public BaseComplexNDArray(double[] data, int[] shape, int[] stride, char ordering) {
        super(data, shape, stride, ordering);
    }

    public BaseComplexNDArray(int[] shape, char ordering) {
        super(shape, ordering);
    }

    @Override
    public IComplexNumber getComplex(int i, IComplexNumber result) {
        return null;
    }

    @Override
    public IComplexNumber getComplex(int i, int j, IComplexNumber result) {
        return null;
    }

    @Override
    public IComplexNDArray putScalar(int j, int i, IComplexNumber conji) {
        return null;
    }

    @Override
    public IComplexNDArray lt(Number other) {
        return NDArrays.createComplex(super.lt(other));
    }

    @Override
    public IComplexNDArray lti(Number other) {
        return NDArrays.createComplex(super.lti(other));
    }

    @Override
    public IComplexNDArray eq(Number other) {
        return NDArrays.createComplex(super.eq(other));
    }

    @Override
    public IComplexNDArray eqi(Number other) {
        return NDArrays.createComplex(super.eqi(other));
    }

    @Override
    public IComplexNDArray gt(Number other) {
        return NDArrays.createComplex(super.gt(other));
    }

    @Override
    public IComplexNDArray gti(Number other) {
        return NDArrays.createComplex(super.gti(other));
    }

    @Override
    public IComplexNDArray lt(INDArray other) {
        return NDArrays.createComplex(super.lt(other));
    }

    @Override
    public IComplexNDArray lti(INDArray other) {
        return NDArrays.createComplex(super.lti(other));
    }

    @Override
    public IComplexNDArray eq(INDArray other) {
        return NDArrays.createComplex(super.eq(other));
    }

    @Override
    public IComplexNDArray eqi(INDArray other) {
        return NDArrays.createComplex(super.eqi(other));
    }

    @Override
    public IComplexNDArray gt(INDArray other) {
        return NDArrays.createComplex(super.gt(other));
    }

    @Override
    public IComplexNDArray gti(INDArray other) {
        return NDArrays.createComplex(super.gti(other));
    }

    /**
     * Initialize the given ndarray as the real component
     * @param m the real component
     * @param stride the stride of the ndarray
     * @param ordering the ordering for the ndarray
     */
    public BaseComplexNDArray(INDArray m,int[] stride,char ordering) {
        this(m.shape(),stride,ordering);
        //NativeBlas.dcopy(m.length, m.data, m.offset(), 1, data, offset, 2);
        INDArray flattened = m.reshape(new int[]{1,m.length()});
        IComplexNDArray flatten = reshape(1,length);
        for(int i = 0; i < length; i++) {
            flatten.put(i, flattened.getScalar(i));
        }
    }


    /** Construct a complex matrix from a realComponent matrix. */
    public BaseComplexNDArray(INDArray m,char ordering) {
        this(m.shape(),ordering);
        //NativeBlas.dcopy(m.length, m.data, m.offset(), 1, data, offset, 2);
        INDArray flattened = m.reshape(new int[]{1,m.length()});
        IComplexNDArray flatten = reshape(1,length);
        for(int i = 0; i < length; i++) {
            flatten.put(i, flattened.getScalar(i));
        }
    }


    /** Construct a complex matrix from a realComponent matrix. */
    public BaseComplexNDArray(INDArray m) {
        this(m,NDArrays.order());
    }

    /**
     * Create with the specified ndarray as the real component
     * and the given stride
     * @param m the ndarray to use as the stride
     * @param stride the stride of the ndarray
     */
    public BaseComplexNDArray(INDArray m,int[] stride) {
        this(m,stride,NDArrays.order());
    }

    /**
     * Create an ndarray from the specified slices
     * and the given shape
     * @param slices the slices of the ndarray
     * @param shape the final shape of the ndarray
     * @param stride the stride of the ndarray
     */
    public BaseComplexNDArray(List<IComplexNDArray> slices,int[] shape,int[] stride) {
        this(slices,shape,stride,NDArrays.order());
    }



    /**
     * Create an ndarray from the specified slices
     * and the given shape
     * @param slices the slices of the ndarray
     * @param shape the final shape of the ndarray
     * @param stride the stride of the ndarray
     * @param ordering the ordering for the ndarray
     *
     */
    public BaseComplexNDArray(List<IComplexNDArray> slices,int[] shape,int[] stride,char ordering) {
        this(new double[ArrayUtil.prod(shape) * 2]);
        List<IComplexNumber> list = new ArrayList<>();
        for(int i = 0; i < slices.size(); i++) {
            IComplexNDArray flattened = slices.get(i).ravel();
            for(int j = 0; j < flattened.length(); j++)
                list.add((IComplexNumber) flattened.getScalar(j).element());
        }


        this.ordering = ordering;
        this.data = new double[ArrayUtil.prod(shape) * 2 ];
        this.stride = stride;
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            data[count] = list.get(i).realComponent().doubleValue();
            data[count + 1] = list.get(i).imaginaryComponent().doubleValue();
            count += 2;
        }

        initShape(shape);



    }

    /**
     * Create an ndarray from the specified slices
     * and the given shape
     * @param slices the slices of the ndarray
     * @param shape the final shape of the ndarray
     * @param ordering the ordering of the ndarray
     */
    public BaseComplexNDArray(List<IComplexNDArray> slices,int[] shape,char ordering) {
        this(slices,shape,ordering == NDArrayFactory.C ? ArrayUtil.calcStrides(shape,2) : ArrayUtil.calcStridesFortran(shape,2),ordering);


    }

    /**
     * Create an ndarray from the specified slices
     * and the given shape
     * @param slices the slices of the ndarray
     * @param shape the final shape of the ndarray
     */
    public BaseComplexNDArray(List<IComplexNDArray> slices,int[] shape) {
        this(slices,shape,NDArrays.order());


    }

    /**
     * Create a complex ndarray with the given complex doubles.
     * Note that this maybe an easier setup than the new double[]
     * @param newData the new data for this array
     * @param shape the shape of the ndarray
     */
    public BaseComplexNDArray(IComplexNumber[] newData,int[] shape) {
        super(new double[ArrayUtil.prod(shape) * 2]);
        initShape(shape);
        for(int i = 0;i  < length; i++)
            put(i, newData[i].asDouble());

    }

    /**
     * Create a complex ndarray with the given complex doubles.
     * Note that this maybe an easier setup than the new double[]
     * @param newData the new data for this array
     * @param shape the shape of the ndarray
     */
    public BaseComplexNDArray(IComplexNumber[] newData,int[] shape,int[] stride) {
        super(new double[ArrayUtil.prod(shape) * 2]);
        this.stride = stride;
        initShape(shape);
        for(int i = 0;i  < length; i++)
            put(i,newData[i].asDouble());

    }



    /**
     * Create a complex ndarray with the given complex doubles.
     * Note that this maybe an easier setup than the new double[]
     * @param newData the new data for this array
     * @param shape the shape of the ndarray
     */
    public BaseComplexNDArray(IComplexDouble[] newData,int[] shape) {
        this(newData,shape,NDArrays.order());

    }

    /**
     * Create a complex ndarray with the given complex doubles.
     * Note that this maybe an easier setup than the new double[]
     * @param newData the new data for this array
     * @param shape the shape of the ndarray
     * @param ordering the ordering for the ndarray
     */
    public BaseComplexNDArray(IComplexDouble[] newData,int[] shape,char ordering) {
        super(new double[ArrayUtil.prod(shape) * 2]);
        this.ordering = ordering;
        initShape(shape);
        for(int i = 0;i  < length; i++)
            put(i,newData[i]);

    }

    /**
     * Initialize with the given data,shape and stride
     * @param data the data to use
     * @param shape the shape of the ndarray
     * @param stride the stride of the ndarray
     */
    public BaseComplexNDArray(double[] data,int[] shape,int[] stride) {
        this(data,shape,stride,0,NDArrays.order());
    }


    /**
     * THe ordering of the ndarray
     * @param data the data to use
     * @param shape the final shape of the ndarray
     * @param stride the stride of the ndarray
     * @param offset the offset
     * @param ordering the ordering
     */
    public BaseComplexNDArray(double[] data,int[] shape,int[] stride,int offset,char ordering) {
        super(data);
        if(offset >= data.length)
            throw new IllegalArgumentException("Invalid offset: must be < data.length");
        this.ordering = ordering;
        this.stride = stride;
        initShape(shape);



        this.offset = offset;



        if(data != null  && data.length > 0)
            this.data = data;
    }



    public BaseComplexNDArray(double[] data,int[] shape,int[] stride,int offset) {
        this(data,shape,stride,offset,NDArrays.order());
    }



    public BaseComplexNDArray(double[] data,int[] shape) {
        this(data,shape,0);
    }


    public BaseComplexNDArray(double[] data,int[] shape,int offset,char ordering) {
        this(data,shape,ordering == NDArrayFactory.C ? calcStrides(shape,2) : calcStridesFortran(shape,2),offset,ordering);
    }

    public BaseComplexNDArray(double[] data,int[] shape,int offset) {
        this(data,shape,offset,NDArrays.order());
    }


    /**
     * Construct an ndarray of the specified shape
     * with an empty data array
     * @param shape the shape of the ndarray
     * @param stride the stride of the ndarray
     * @param offset the desired offset
     */
    public BaseComplexNDArray(int[] shape,int[] stride,int offset) {
        this(new double[ArrayUtil.prod(shape) * 2],shape,stride,offset);
    }

    /**
     * Construct an ndarray of the specified shape
     * with an empty data array
     * @param shape the shape of the ndarray
     * @param stride the stride of the ndarray
     * @param offset the desired offset
     * @param ordering the ordering for the ndarray
     */
    public BaseComplexNDArray(int[] shape,int[] stride,int offset,char ordering) {
        this(new double[ArrayUtil.prod(shape) * 2],shape,stride,offset);
    }



    /**
     * Create the ndarray with
     * the specified shape and stride and an offset of 0
     * @param shape the shape of the ndarray
     * @param stride the stride of the ndarray
     */
    public BaseComplexNDArray(int[] shape,int[] stride,char ordering){
        this(shape,stride,0,ordering);
    }


    /**
     * Create the ndarray with
     * the specified shape and stride and an offset of 0
     * @param shape the shape of the ndarray
     * @param stride the stride of the ndarray
     */
    public BaseComplexNDArray(int[] shape,int[] stride){
        this(shape,stride,0);
    }

    /**
     *
     * @param shape
     * @param offset
     */
    public BaseComplexNDArray(int[] shape,int offset) {
        this(shape,offset,NDArrays.order());
    }




    /**
     * Creates a new <i>n</i> times <i>m</i> <tt>ComplexDoubleMatrix</tt>.
     *
     * @param newRows    the number of rows (<i>n</i>) of the new matrix.
     * @param newColumns the number of columns (<i>m</i>) of the new matrix.
     */
    public BaseComplexNDArray(int newRows, int newColumns) {
        this(new int[]{newRows,newColumns});
    }

    /**
     * Creates a new <i>n</i> times <i>m</i> <tt>ComplexDoubleMatrix</tt>.
     *
     * @param newRows    the number of rows (<i>n</i>) of the new matrix.
     * @param newColumns the number of columns (<i>m</i>) of the new matrix.
     * @param ordering the ordering of the ndarray
     */
    public BaseComplexNDArray(int newRows, int newColumns,char ordering) {
        this(new int[]{newRows,newColumns},ordering);
    }


    /**
     * Float overloading for constructor
     * @param data the data to use
     * @param shape the shape to use
     * @param stride the stride of the ndarray
     * @param offset the offset of the ndarray
     * @param ordering the ordering for the ndarrayg
     */
    public BaseComplexNDArray(float[] data, int[] shape, int[] stride, int offset,char ordering) {
        this(ArrayUtil.doubleCopyOf(data),shape,stride,offset,ordering);
    }


    public BaseComplexNDArray(float[] data, int[] shape, int[] stride, int offset) {
        this(data,shape,stride,offset,NDArrays.order());
    }

    public BaseComplexNDArray(double[] doubles) {
        super(doubles);
    }

    @Override
    public IComplexNDArray dup() {
        double[] dupData = new double[data.length];
        System.arraycopy(data,0,dupData,0,dupData.length);
        IComplexNDArray ret = NDArrays.createComplex(dupData,shape,stride,offset,ordering);
        return ret;
    }



    /**
     * Returns the squared (Euclidean) distance.
     */
    public double squaredDistance(INDArray other) {
        double sd = 0.0;
        for (int i = 0; i < length; i++) {
            IComplexNumber diff = (IComplexNumber) getScalar(i).sub(other.getScalar(i)).element();
            double d = (double) diff.absoluteValue();
            sd += d * d;
        }
        return sd;
    }

    /**
     * Returns the (euclidean) distance.
     */
    public double distance2(INDArray other) {
        return  Math.sqrt(squaredDistance(other));
    }

    /**
     * Returns the (1-norm) distance.
     */
    public double distance1(INDArray other) {
        double d = 0.0;
        for (int i = 0; i < length; i++) {
            IComplexNumber n = (IComplexNumber) getScalar(i).sub(other.getScalar(i)).element();
            d += n.absoluteValue().doubleValue();
        }
        return d;
    }

    @Override
    public INDArray put(NDArrayIndex[] indices, INDArray element) {
        return null;
    }


    /**
     * Inserts the element at the specified index
     *
     * @param i       the row insert into
     * @param j       the column to insert into
     * @param element a scalar ndarray
     * @return a scalar ndarray of the element at this index
     */
    @Override
    public INDArray put(int i, int j, Number element) {
        return put(i,j,NDArrays.scalar(element));
    }


    /**
     * @param indexes
     * @param value
     * @return
     */
    @Override
    public IComplexNDArray put(int[] indexes, double value) {
        int ix = offset;
        if (indexes.length != shape.length)
            throw new IllegalArgumentException("Unable to applyTransformToDestination values: number of indices must be equal to the shape");

        for (int i = 0; i< shape.length; i++)
            ix += indexes[i] * stride[i];


        data[ix] = value;
        return this;
    }


    /**
     * Assigns the given matrix (put) to the specified slice
     * @param slice the slice to assign
     * @param put the slice to applyTransformToDestination
     * @return this for chainability
     */
    @Override
    public IComplexNDArray putSlice(int slice, IComplexNDArray put) {
        if(isScalar()) {
            assert put.isScalar() : "Invalid dimension. Can only insert a scalar in to another scalar";
            put(0,put.get(0));
            return this;
        }

        else if(isVector()) {
            assert put.isScalar() : "Invalid dimension on insertion. Can only insert scalars input vectors";
            put(slice,put.get(0));
            return this;
        }


        assertSlice(put,slice);


        IComplexNDArray view = slice(slice);

        if(put.isScalar())
            put(slice,put.get(0));
        else if(put.isVector())
            for(int i = 0; i < put.length(); i++)
                view.putScalar(i, put.getComplex(i));
        else if(put.shape().length == 2)
            for(int i = 0; i < put.rows(); i++)
                for(int j = 0; j < put.columns(); j++)
                    view.put(i,j,put.get(i,j));

        else {

            assert put.slices() == view.slices() : "Slices must be equivalent.";
            for(int i = 0; i < put.slices(); i++)
                view.slice(i).putSlice(i,view.slice(i));

        }

        return this;

    }





    /**
     * Mainly here for people coming from numpy.
     * This is equivalent to a call to permute
     * @param dimension the dimension to swap
     * @param with the one to swap it with
     * @return the swapped axes view
     */
    public IComplexNDArray swapAxes(int dimension,int with) {
        int[] shape = ArrayUtil.range(0,shape().length);
        shape[dimension] = with;
        shape[with] = dimension;
        return permute(shape);
    }





    /**
     * Compute complex conj (in-place).
     */
    @Override
    public IComplexNDArray conji() {
        IComplexNDArray reshaped = reshape(1,length);
        IComplexDouble c = NDArrays.createDouble(0.0,0);
        for (int i = 0; i < length; i++)
            reshaped.putScalar(i, reshaped.getComplex(i, c).conji());
        return this;
    }

    @Override
    public IComplexNDArray hermitian() {
        IComplexNDArray result = NDArrays.createComplex(shape());

        IComplexDouble c = NDArrays.createDouble(0,0);

        for (int i = 0; i < slices(); i++)
            for (int j = 0; j < columns; j++)
                result.putScalar(j, i, getComplex(i, j, c).conji());
        return result;
    }

    /**
     * Compute complex conj.
     */
    @Override
    public IComplexNDArray conj() {
        return dup().conji();
    }

    @Override
    public INDArray getReal() {
        int[] stride = ArrayUtil.copy(stride());
        for(int i = 0; i < stride.length; i++)
            stride[i] /= 2;
        INDArray result = NDArrays.create(shape(), stride);
        NDArrays.getBlasWrapper().dcopy(length, data, offset, 2, result.data(), 0, 1);
        return result;
    }

    @Override
    public double getImag(int i) {
        return data[2 * i + offset + 1];
    }

    @Override
    public double getReal(int i) {
        return data[2 * i  + offset];
    }

    @Override
    public IComplexNDArray putReal(int rowIndex, int columnIndex, double value) {
        data[2 * index(rowIndex, columnIndex) + offset] = value;
        return this;
    }

    @Override
    public IComplexNDArray putImag(int rowIndex, int columnIndex, double value) {
        data[2*index(rowIndex, columnIndex) + 1 + offset] = value;
        return this;
    }

    @Override
    public IComplexNDArray putReal(int i, double v) {
        int idx = linearIndex(i);
        data[idx] = v;
        return this;
    }

    @Override
    public IComplexNDArray putImag(int i, double v) {
        int idx = linearIndex(i);
        data[idx + 1] = v;
        return this;
    }


    @Override
    public IComplexNumber getComplex(int i) {
        int idx = linearIndex(i);
        return NDArrays.createDouble(data[idx],data[idx + 1]);
    }

    @Override
    public IComplexNumber getComplex(int i, int j) {
        int idx = index(i,j);
        return NDArrays.createDouble(data[idx],data[idx + 1]);

    }

    /**
     * Get realComponent part of the matrix.
     */
    @Override
    public INDArray real() {
        INDArray ret = NDArrays.create(shape);
        NDArrays.getBlasWrapper().dcopy(length, data, 0, 2, ret.data(), 0, 1);
        return ret;
    }

    /**
     * Get imaginary part of the matrix.
     */
    @Override
    public INDArray imag() {
        INDArray ret = NDArrays.create(shape);
        NDArrays.getBlasWrapper().dcopy(length, data, 1, 2, ret.data(), 0, 1);
        return ret;
    }



    public DimensionSlice getNextN(int from,int num) {
        List<Integer> list = new ArrayList<>();
        for(int i = from; i < from + num + 1; i++) {
            //realComponent and imaginary
            list.add(i);
            list.add(i + 1);
        }

        return new DimensionSlice(
                false,
                NDArrays.createComplex(data,new int[]{2},new int[]{1}),
                ArrayUtil.toArray(list));
    }


    /**
     * Iterate along a dimension.
     * This encapsulates the process of sum, mean, and other processes
     * take when iterating over a dimension.
     * @param dimension the dimension to iterate over
     * @param op the operation to apply
     * @param modify whether to modify this array or not based on the results
     */
    public void iterateOverDimension(int dimension,SliceOp op,boolean modify) {
        if(isScalar()) {
            if(dimension > 1)
                throw new IllegalArgumentException("Dimension must be 0 for a scalar");
            else {
                DimensionSlice slice = this.vectorForDimensionAndOffset(0,0);
                op.operate(slice);

                if(modify && slice.getIndices() != null) {
                    IComplexNDArray result = (IComplexNDArray) slice.getResult();
                    for(int i = 0; i < slice.getIndices().length; i++) {
                        data[slice.getIndices()[i]] = result.getComplex(i).realComponent().doubleValue();
                        data[slice.getIndices()[i] + 1] = result.getComplex(i).imaginaryComponent().doubleValue();
                    }
                }
            }
        }



        else if(isVector()) {
            if(dimension == 0) {
                DimensionSlice slice = vectorForDimensionAndOffset(0,0);
                op.operate(slice);
                if(modify && slice.getIndices() != null) {
                    IComplexNDArray result = (IComplexNDArray) slice.getResult();
                    for(int i = 0; i < slice.getIndices().length; i++) {
                        data[slice.getIndices()[i]] = result.getComplex(i).realComponent().doubleValue();
                        data[slice.getIndices()[i] + 1] = result.getComplex(i).imaginaryComponent().doubleValue();
                    }
                }
            }
            else if(dimension == 1) {
                for(int i = 0; i < length; i++) {
                    DimensionSlice slice = vectorForDimensionAndOffset(dimension,i);
                    op.operate(slice);
                    if(modify && slice.getIndices() != null) {
                        IComplexNDArray result = (IComplexNDArray) slice.getResult();
                        for(int j = 0; j < slice.getIndices().length; j++) {
                            data[slice.getIndices()[j]] = result.getComplex(j).realComponent().doubleValue();
                            data[slice.getIndices()[j] + 1] = result.getComplex(j).imaginaryComponent().doubleValue();
                        }
                    }
                }
            }
            else
                throw new IllegalArgumentException("Illegal dimension for vector " + dimension);
        }

        else {
            if(dimension >= shape.length)
                throw new IllegalArgumentException("Unable to remove dimension  " + dimension + " was >= shape length");


            int[] shape = ArrayUtil.removeIndex(this.shape,dimension);

            if(dimension == 0) {
                //iterating along the dimension is relative to the number of slices
                //in the return dimension
                int numTimes = ArrayUtil.prod(shape);
                //note difference here from ndarray, the offset is incremented by 2 every time
                //note also numtimes is multiplied by 2, this is due to the complex and imaginary components
                for(int offset = this.offset; offset < numTimes ; offset += 2) {
                    DimensionSlice vector = vectorForDimensionAndOffset(dimension,offset);
                    op.operate(vector);
                    if(modify && vector.getIndices() != null) {
                        IComplexNDArray result = (IComplexNDArray) vector.getResult();
                        for(int i = 0; i < vector.getIndices().length; i++) {
                            data[vector.getIndices()[i]] = result.getComplex(i).realComponent().doubleValue();
                            data[vector.getIndices()[i] + 1] = result.getComplex(i).imaginaryComponent().doubleValue();
                        }
                    }

                }

            }

            else {
                //needs to be 2 * shape: this is due to both realComponent and imaginary components
                double[] data2 = new double[ArrayUtil.prod(shape) ];
                int dataIter = 0;
                //want the milestone to slice[1] and beyond
                int[] sliceIndices = endsForSlices();
                int currOffset = 0;

                //iterating along the dimension is relative to the number of slices
                //in the return dimension
                //note here the  and +=2 this is for iterating over realComponent and imaginary components
                for(int offset = this.offset;;) {
                    if(dataIter >= data2.length || currOffset >= sliceIndices.length)
                        break;

                    //do the operation,, and look for whether it exceeded the current slice
                    DimensionSlice pair = vectorForDimensionAndOffsetPair(dimension, offset,sliceIndices[currOffset]);
                    //append the result
                    op.operate(pair);


                    if(modify && pair.getIndices() != null) {
                        IComplexNDArray result = (IComplexNDArray) pair.getResult();
                        for(int i = 0; i < pair.getIndices().length; i++) {
                            data[pair.getIndices()[i]] = result.getComplex(i).realComponent().doubleValue();
                            data[pair.getIndices()[i] + 1] = result.getComplex(i).imaginaryComponent().doubleValue();
                        }
                    }

                    //go to next slice and iterate over that
                    if(pair.isNextSlice()) {
                        //DO NOT CHANGE
                        currOffset++;
                        if(currOffset >= sliceIndices.length)
                            break;
                        //will update to next step
                        offset = sliceIndices[currOffset];
                    }

                }

            }


        }


    }



    //getFromOrigin one result along one dimension based on the given offset
    public DimensionSlice vectorForDimensionAndOffsetPair(int dimension, int offset,int currOffsetForSlice) {
        int count = 0;
        IComplexNDArray ret = NDArrays.createComplex(new int[]{shape[dimension]});
        boolean newSlice = false;
        List<Integer> indices = new ArrayList<>();
        for(int j = offset; count < this.shape[dimension]; j+= this.stride[dimension] ) {
            IComplexDouble d = NDArrays.createDouble(data[j], data[j + 1]);
            indices.add(j);
            ret.putScalar(count++, d);
            if(j >= currOffsetForSlice)
                newSlice = true;

        }

        return new DimensionSlice(newSlice,ret,ArrayUtil.toArray(indices));
    }


    //getFromOrigin one result along one dimension based on the given offset
    public DimensionSlice vectorForDimensionAndOffset(int dimension, int offset) {
        if(isScalar() && dimension == 0 && offset == 0)
            return new DimensionSlice(false,NDArrays.complexScalar(get(offset)),new int[]{offset});


            //need whole vector
        else  if (isVector()) {
            if(dimension == 0) {
                int[] indices = new int[length];
                for(int i = 0; i < indices.length; i++)
                    indices[i] = i;
                return new DimensionSlice(false,dup(),indices);
            }

            else if(dimension == 1) {
                return new DimensionSlice(false,NDArrays.complexScalar(get(offset)),new int[]{offset});
            }

            else
                throw new IllegalArgumentException("Illegal dimension for vector " + dimension);
        }


        else {
            int count = 0;
            IComplexNDArray ret = NDArrays.createComplex(new int[]{shape[dimension]});
            List<Integer> indices = new ArrayList<>();
            for (int j = offset; count < this.shape[dimension]; j += this.stride[dimension] ) {
                IComplexDouble d = NDArrays.createDouble(data[j], data[j + 1]);
                ret.putScalar(count++, d);
                indices.add(j);
            }

            return new DimensionSlice(false, ret, ArrayUtil.toArray(indices));
        }

    }




    //getFromOrigin one result along one dimension based on the given offset
    private ComplexIterationResult op(int dimension, int offset, Ops.DimensionOp op,int currOffsetForSlice) {
        double[] dim = new double[this.shape[dimension]];
        int count = 0;
        boolean newSlice = false;
        for(int j = offset; count < dim.length; j+= this.stride[dimension]) {
            double d = data[j];
            dim[count++] = d;
            if(j >= currOffsetForSlice)
                newSlice = true;
        }

        IComplexNDArray r = NDArrays.createComplex(dim);
        IComplexDouble r2 = reduceVector(op,r);
        return new ComplexIterationResult(newSlice,r2);
    }


    //getFromOrigin one result along one dimension based on the given offset
    private IComplexDouble op(int dimension, int offset, Ops.DimensionOp op) {
        double[] dim = new double[this.shape[dimension]];
        int count = 0;
        for(int j = offset; count < dim.length; j+= this.stride[dimension]) {
            double d = data[j];
            dim[count++] = d;
        }

        return reduceVector(op,NDArrays.createComplex(dim));
    }





    private IComplexDouble reduceVector(Ops.DimensionOp op,IComplexNDArray vector) {

        switch(op) {
            case SUM:
                return (IComplexDouble) vector.sum(Integer.MAX_VALUE).element();
            case MEAN:
                return (IComplexDouble) vector.mean(Integer.MAX_VALUE).element();
            case NORM_1:
                return NDArrays.createDouble((double) vector.norm1(Integer.MAX_VALUE).element(), 0);
            case NORM_2:
                return NDArrays.createDouble((double) vector.norm2(Integer.MAX_VALUE).element(),0);
            case NORM_MAX:
                return NDArrays.createDouble((double) vector.normmax(Integer.MAX_VALUE).element(),0);
            case FFT:
            default: throw new IllegalArgumentException("Illegal operation");
        }
    }



    /**
     * Fetch a particular number on a multi dimensional scale.
     *
     * @param indexes the indexes to getFromOrigin a number from
     * @return the number at the specified indices
     */
    @Override
    public IComplexNDArray getScalar(int... indexes) {
        int ix = offset;
        for (int i = 0; i < shape.length; i++) {
            ix += indexes[i] * stride[i];
        }
        return NDArrays.scalar(NDArrays.createDouble(data[ix],data[ix + 1]));
    }

    /**
     * Validate dimensions are equal
     *
     * @param other the other ndarray to compare
     */
    @Override
    public void checkDimensions(INDArray other) {

    }

    /**
     * Gives the indices for the ending of each slice
     * @return the off sets for the beginning of each slice
     */
    public int[] endsForSlices() {
        int[] ret = new int[slices()];
        int currOffset = offset;
        for(int i = 0; i < slices(); i++) {
            ret[i] = (currOffset );
            currOffset += stride[0];
        }
        return ret;
    }

    /**
     * http://docs.scipy.org/doc/numpy/reference/generated/numpy.ufunc.reduce.html
     *
     * @param op        the operation to do
     * @param dimension the dimension to return from
     * @return the results of the reduce (applying the operation along the specified
     * dimension)t
     */
    @Override
    public IComplexNDArray reduce(Ops.DimensionOp op, int dimension) {
        if(isScalar())
            return this;


        if(isVector())
            return NDArrays.scalar(reduceVector(op, this));


        int[] shape = ArrayUtil.removeIndex(this.shape,dimension);

        if(dimension == 0) {
            double[] data2 = new double[ArrayUtil.prod(shape) * 2];
            int dataIter = 0;

            //iterating along the dimension is relative to the number of slices
            //in the return dimension
            int numTimes = ArrayUtil.prod(shape);
            for(int offset = this.offset; offset < numTimes; offset++) {
                IComplexDouble reduce = op(dimension, offset, op);
                data2[dataIter++] = reduce.realComponent();
                data2[dataIter++] = reduce.imaginaryComponent();


            }

            return NDArrays.createComplex(data2,shape);
        }

        else {
            double[] data2 = new double[ArrayUtil.prod(shape)];
            int dataIter = 0;
            //want the milestone to slice[1] and beyond
            int[] sliceIndices = endsForSlices();
            int currOffset = 0;

            //iterating along the dimension is relative to the number of slices
            //in the return dimension
            int numTimes = ArrayUtil.prod(shape);
            for(int offset = this.offset; offset < numTimes; offset++) {
                if(dataIter >= data2.length || currOffset >= sliceIndices.length)
                    break;

                //do the operation,, and look for whether it exceeded the current slice
                ComplexIterationResult pair = op(dimension, offset, op,sliceIndices[currOffset]);
                //append the result
                IComplexNumber reduce = pair.getNumber();
                data2[dataIter++] = reduce.realComponent().doubleValue();
                data2[dataIter++] = reduce.imaginaryComponent().doubleValue();
                //go to next slice and iterate over that
                if(pair.isNextIteration()) {
                    //will update to next step
                    offset = sliceIndices[currOffset];
                    numTimes +=  sliceIndices[currOffset];
                    currOffset++;
                }

            }

            return NDArrays.createComplex(data2,shape);
        }

    }

    /**
     * Assigns the given matrix (put) to the specified slice
     *
     * @param slice the slice to assign
     * @param put   the slice to applyTransformToDestination
     * @return this for chainability
     */
    @Override
    public IComplexNDArray putSlice(int slice, INDArray put) {
        if(isScalar()) {
            assert put.isScalar() : "Invalid dimension. Can only insert a scalar in to another scalar";
            put(0,put.getScalar(0));
            return this;
        }

        else if(isVector()) {
            assert put.isScalar() : "Invalid dimension on insertion. Can only insert scalars input vectors";
            put(slice,put.getScalar(0));
            return this;
        }


        assertSlice(put,slice);


        INDArray view = slice(slice);

        if(put.isScalar())
            put(slice,put.getScalar(0));
        else if(put.isVector())
            for(int i = 0; i < put.length(); i++)
                view.put(i,put.getScalar(i));
        else if(put.shape().length == 2)
            for(int i = 0; i < put.rows(); i++)
                for(int j = 0; j < put.columns(); j++) {
                    view.put(i,j,NDArrays.scalar((IComplexNumber) put.getScalar(i, j).element()));

                }

        else {

            assert put.slices() == view.slices() : "Slices must be equivalent.";
            for(int i = 0; i < put.slices(); i++)
                view.slice(i).putSlice(i,view.slice(i));

        }

        return this;
    }



    @Override
    public IComplexNDArray subArray(int[] shape) {
        return subArray(offsetsForSlices(),shape);
    }





    @Override
    public IComplexNDArray subArray(int[] offsets, int[] shape,int[] stride) {
        int n = shape.length;
        if (offsets.length != n)
            throw new IllegalArgumentException("Invalid offset " + Arrays.toString(offsets));
        if (shape.length != n)
            throw new IllegalArgumentException("Invalid shape " + Arrays.toString(shape));

        if (Arrays.equals(shape, this.shape)) {
            if (ArrayUtil.isZero(offsets)) {
                return this;
            } else {
                throw new IllegalArgumentException("Invalid subArray offsets");
            }
        }

        return NDArrays.createComplex(
                data
                , Arrays.copyOf(shape,shape.length)
                , stride
                ,offset + ArrayUtil.dotProduct(offsets, stride)
        );
    }




    @Override
    public IComplexNDArray subArray(int[] offsets, int[] shape) {
        return subArray(offsets,shape,stride);
    }











    /**
     * Inserts the element at the specified index
     *
     * @param indices the indices to insert into
     * @param element a scalar ndarray
     * @return a scalar ndarray of the element at this index
     */
    @Override
    public IComplexNDArray put(int[] indices, INDArray element) {
        if(!element.isScalar())
            throw new IllegalArgumentException("Unable to insert anything but a scalar");
        int ix = offset;
        if (indices.length != shape.length)
            throw new IllegalArgumentException("Unable to applyTransformToDestination values: number of indices must be equal to the shape");

        for (int i = 0; i< shape.length; i++)
            ix += indices[i] * stride[i];

        if(element instanceof IComplexNDArray) {
            IComplexNumber element2 = (IComplexNumber) element.element();
            data[ix] = (double) element2.realComponent();
            data[ix + 1]= (double) element2.imaginaryComponent();
        }
        else {
            double element2 = (double) element.element();
            data[ix] = element2;
            data[ix + 1]= 0;
        }

        return this;

    }

    /**
     * Inserts the element at the specified index
     *
     * @param i       the row insert into
     * @param j       the column to insert into
     * @param element a scalar ndarray
     * @return a scalar ndarray of the element at this index
     */
    @Override
    public IComplexNDArray put(int i, int j, INDArray element) {
        return put(new int[]{i,j},element);
    }




    /**
     * Returns the specified slice of this matrix.
     * In matlab, this would be equivalent to (given a 2 x 2 x 2):
     * A(:,:,x) where x is the slice you want to return.
     *
     * The slice is always relative to the final dimension of the matrix.
     *
     * @param slice the slice to return
     * @return the specified slice of this matrix
     */
    public IComplexNDArray slice(int slice) {
        int offset = this.offset + (slice * stride[0]  );
        IComplexNDArray ret;
        if (shape.length == 0)
            throw new IllegalArgumentException("Can't slice a 0-d ComplexNDArray");

            //slice of a vector is a scalar
        else if (shape.length == 1)
            ret = NDArrays.createComplex(
                    data,
                    ArrayUtil.empty(),
                    ArrayUtil.empty(),
                    offset,ordering);


            //slice of a matrix is a vector
        else if (shape.length == 2) {
            ret = NDArrays.createComplex(
                    data,
                    ArrayUtil.of(shape[1]),
                    Arrays.copyOfRange(stride,1,stride.length),
                    offset,ordering

            );

        }

        else {
            if(offset >= data.length)
                throw new IllegalArgumentException("Offset index is > data.length");
            ret = NDArrays.createComplex(data,
                    Arrays.copyOfRange(shape, 1, shape.length),
                    Arrays.copyOfRange(stride, 1, stride.length),
                    offset,ordering);
        }

        return ret;
    }


    /**
     * Returns the slice of this from the specified dimension
     * @param slice the dimension to return from
     * @param dimension the dimension of the slice to return
     * @return the slice of this matrix from the specified dimension
     * and dimension
     */
    @Override
    public IComplexNDArray slice(int slice, int dimension) {
        int offset = this.offset + dimension * stride[slice];
        if(this.offset == 0)
            offset *= 2;
        IComplexNDArray ret;
        if (shape.length == 2) {
            int st = stride[1];
            if (st == 1) {
                return NDArrays.createComplex(
                        data,
                        new int[]{shape[dimension]},
                        offset,ordering);
            } else {
                return NDArrays.createComplex(
                        data,
                        new int[]{shape[dimension]},
                        new int[]{st},
                        offset);
            }


        }

        if (slice == 0)
            return slice(dimension);


        return NDArrays.createComplex (
                data,
                ArrayUtil.removeIndex(shape,dimension),
                ArrayUtil.removeIndex(stride,dimension),
                offset,ordering
        );
    }




    /**
     * Replicate and tile array to fill out to the given shape
     *
     * @param shape the new shape of this ndarray
     * @return the shape to fill out to
     */
    @Override
    public IComplexNDArray repmat(int[] shape) {
        int[] newShape = ArrayUtil.copy(shape());
        assert shape.length <= newShape.length : "Illegal shape: The passed in shape must be <= the current shape length";
        for(int i = 0; i < shape.length; i++)
            newShape[i] *= shape[i];
        IComplexNDArray result = NDArrays.createComplex(newShape);
        //nd copy
        if(isScalar()) {
            for(int i = 0; i < result.length(); i++) {
                result.put(i,getScalar(0));

            }
        }

        else if(isMatrix()) {

            for (int c = 0; c < shape()[1]; c++) {
                for (int r = 0; r < shape()[0]; r++) {
                    for (int i = 0; i < rows(); i++) {
                        for (int j = 0; j < columns(); j++) {
                            result.put(r * rows() + i, c * columns() + j, getScalar(i, j));
                        }
                    }
                }
            }

        }

        else {
            int[] sliceRepmat = ArrayUtil.removeIndex(shape,0);
            for(int i = 0; i < result.slices(); i++) {
                result.putSlice(i,repmat(sliceRepmat));
            }
        }

        return  result;
    }



    /**
     * Assign all of the elements in the given
     * ndarray to this nedarray
     *
     * @param arr the elements to assign
     * @return this
     */
    @Override
    public IComplexNDArray assign(IComplexNDArray arr) {
        LinAlgExceptions.assertSameShape(this, arr);
        INDArray other = arr.ravel();
        INDArray thisArr = ravel();
        for(int i = 0; i < other.length(); i++)
            thisArr.put(i,other.getScalar(i));
        return this;
    }
    /**
     * Get whole rows from the passed indices.
     *
     * @param rindices
     */
    @Override
    public IComplexNDArray getRows(int[] rindices) {
        INDArray rows = NDArrays.create(rindices.length,columns());
        for(int i = 0; i < rindices.length; i++) {
            rows.putRow(i,getRow(rindices[i]));
        }
        return (IComplexNDArray) rows;
    }





    @Override
    public IComplexNDArray put(NDArrayIndex[] indices, IComplexNumber element) {
        return null;
    }

    @Override
    public IComplexNDArray put(NDArrayIndex[] indices, IComplexNDArray element) {
        return null;
    }

    @Override
    public IComplexNDArray put(NDArrayIndex[] indices, Number element) {
        return null;
    }

    @Override
    public IComplexNDArray putScalar(int i, IComplexNumber value) {
        return put(i, NDArrays.scalar(value));
    }



    /**
     * Get the vector along a particular dimension
     *
     * @param index     the index of the vector to get
     * @param dimension the dimension to get the vector from
     * @return the vector along a particular dimension
     */
    @Override
    public IComplexNDArray vectorAlongDimension(int index, int dimension) {
        return NDArrays.createComplex(data,
                new int[]{shape[dimension]}
                ,new int[]{stride[dimension]},
                offset + index,ordering);
    }

    /**
     * Cumulative sum along a dimension
     *
     * @param dimension the dimension to perform cumulative sum along
     * @return the cumulative sum along the specified dimension
     */
    @Override
    public IComplexNDArray cumsumi(int dimension) {
        if(isVector()) {
            IComplexNumber s = NDArrays.createDouble(0,0);
            for (int i = 0; i < length; i++) {
                s .addi((IComplexNumber) getScalar(i).element());
                putScalar(i, s);
            }
        }

        else if(dimension == Integer.MAX_VALUE || dimension == shape.length - 1) {
            IComplexNDArray flattened = ravel().dup();
            IComplexNumber prevVal = (IComplexNumber) flattened.getScalar(0).element();
            for(int i = 1; i < flattened.length(); i++) {
                IComplexNumber d = prevVal.add((IComplexNumber) flattened.getScalar(i).element());
                flattened.putScalar(i,d);
                prevVal = d;
            }

            return flattened;
        }



        else {
            for(int i = 0; i < vectorsAlongDimension(dimension); i++) {
                IComplexNDArray vec = vectorAlongDimension(i,dimension);
                vec.cumsumi(0);

            }
        }


        return this;
    }

    /**
     * Cumulative sum along a dimension (in place)
     *
     * @param dimension the dimension to perform cumulative sum along
     * @return the cumulative sum along the specified dimension
     */
    @Override
    public IComplexNDArray cumsum(int dimension) {
        return dup().cumsumi(dimension);
    }

    /**
     * Assign all of the elements in the given
     * ndarray to this nedarray
     *
     * @param arr the elements to assign
     * @return this
     */
    @Override
    public INDArray assign(INDArray arr) {
        return null;
    }

    @Override
    public IComplexNDArray putScalar(int i, Number value) {
        return put(i, NDArrays.scalar(value));
    }

    @Override
    public INDArray putScalar(int[] i, Number value) {
        return null;
    }

    /**
     * Negate each element.
     */
    @Override
    public IComplexNDArray neg() {
        return dup().negi();
    }

    /**
     * Negate each element (in-place).
     */
    @Override
    public IComplexNDArray negi() {
        return  Transforms.neg(this);
    }

    @Override
    public IComplexNDArray rdiv(Number n) {
        return null;
    }

    @Override
    public IComplexNDArray rdivi(Number n) {
        return null;
    }

    @Override
    public IComplexNDArray rsub(Number n) {
        return null;
    }

    @Override
    public IComplexNDArray rsubi(Number n) {
        return null;
    }


    @Override
    public IComplexNDArray div(Number n) {
        return dup().divi(n);
    }

    @Override
    public IComplexNDArray divi(Number n) {
        return divi(NDArrays.scalar(n));
    }

    @Override
    public IComplexNDArray mul(Number n) {
        return dup().muli(n);
    }

    @Override
    public IComplexNDArray muli(Number n) {
        return muli(NDArrays.scalar(n));
    }

    @Override
    public IComplexNDArray sub(Number n) {
        return dup().subi(n);
    }

    @Override
    public IComplexNDArray subi(Number n) {
        return subi(NDArrays.scalar(n));
    }

    @Override
    public IComplexNDArray add(Number n) {
        return dup().addi(n);
    }

    @Override
    public IComplexNDArray addi(Number n) {
        return addi(NDArrays.scalar(n));
    }

    /**
     * Returns a subset of this array based on the specified
     * indexes
     *
     * @param indexes the indexes in to the array
     * @return a view of the array with the specified indices
     */
    @Override
    public IComplexNDArray get(NDArrayIndex... indexes) {
        return null;
    }

    /**
     * Get whole columns from the passed indices.
     *
     * @param cindices
     */
    @Override
    public IComplexNDArray getColumns(int[] cindices) {
        IComplexNDArray rows = NDArrays.createComplex(rows(),cindices.length);
        for(int i = 0; i < cindices.length; i++) {
            rows.putColumn(i,getColumn(cindices[i]));
        }
        return  rows;
    }


    /**
     * Insert a row in to this array
     * Will throw an exception if this
     * ndarray is not a matrix
     *
     * @param row   the row insert into
     * @param toPut the row to insert
     * @return this
     */
    @Override
    public IComplexNDArray putRow(int row, INDArray toPut) {
        super.putRow(row, toPut);
        return this;
    }

    /**
     * Insert a column in to this array
     * Will throw an exception if this
     * ndarray is not a matrix
     *
     * @param column the column to insert
     * @param toPut  the array to put
     * @return this
     */
    @Override
    public IComplexNDArray putColumn(int column, INDArray toPut) {
         super.putColumn(column, toPut);
        return this;
    }

    /**
     * Returns the element at the specified row/column
     * This will throw an exception if the
     *
     * @param row    the row of the element to return
     * @param column the row of the element to return
     * @return a scalar indarray of the element at this index
     */
    @Override
    public IComplexNDArray getScalar(int row, int column) {
        return  getScalar(row, column);
    }




    /**
     * Returns the element at the specified index
     *
     * @param i the index of the element to return
     * @return a scalar ndarray of the element at this index
     */
    @Override
    public IComplexNDArray getScalar(int i) {
        return NDArrays.complexScalar(get(i));
    }

    /**
     * Inserts the element at the specified index
     *
     * @param i       the index insert into
     * @param element a scalar ndarray
     * @return a scalar ndarray of the element at this index
     */
    @Override
    public IComplexNDArray put(int i, INDArray element) {
        if(element == null)
            throw new IllegalArgumentException("Unable to insert null element");
        assert element.isScalar() : "Unable to insert non scalar element";
        if(element instanceof  IComplexNDArray) {
            put(i,(IComplexNumber) element.element());
        }
        else
            put(i,(double) element.element());
        return this;
    }

    private void put(int i, double element) {
    }

    public void put(int i, IComplexNumber element) {
    }

    /**
     * In place addition of a column vector
     *
     * @param columnVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray diviColumnVector(INDArray columnVector) {
        for(int i = 0; i < columns(); i++) {
            getColumn(i).divi(columnVector.getScalar(i));
        }
        return this;
    }

    /**
     * In place addition of a column vector
     *
     * @param columnVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray divColumnVector(INDArray columnVector) {
        return dup().diviColumnVector(columnVector);
    }

    /**
     * In place addition of a column vector
     *
     * @param rowVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray diviRowVector(INDArray rowVector) {
        for(int i = 0; i < rows(); i++) {
            getRow(i).divi(rowVector.getScalar(i));
        }
        return this;
    }

    /**
     * In place addition of a column vector
     *
     * @param rowVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray divRowVector(INDArray rowVector) {
        return dup().diviRowVector(rowVector);
    }

    /**
     * In place addition of a column vector
     *
     * @param columnVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray muliColumnVector(INDArray columnVector) {
        for(int i = 0; i < columns(); i++) {
            getColumn(i).muli(columnVector.getScalar(i));
        }
        return this;
    }

    /**
     * In place addition of a column vector
     *
     * @param columnVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray mulColumnVector(INDArray columnVector) {
        return dup().muliColumnVector(columnVector);
    }

    /**
     * In place addition of a column vector
     *
     * @param rowVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray muliRowVector(INDArray rowVector) {
        for(int i = 0; i < rows(); i++) {
            getRow(i).muli(rowVector.getScalar(i));
        }
        return this;
    }

    /**
     * In place addition of a column vector
     *
     * @param rowVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray mulRowVector(INDArray rowVector) {
        return dup().muliRowVector(rowVector);
    }

    /**
     * In place addition of a column vector
     *
     * @param columnVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray subiColumnVector(INDArray columnVector) {
        for(int i = 0; i < columns(); i++) {
            getColumn(i).subi(columnVector.getScalar(i));
        }
        return this;
    }

    /**
     * In place addition of a column vector
     *
     * @param columnVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray subColumnVector(INDArray columnVector) {
        return dup().subiColumnVector(columnVector);
    }

    /**
     * In place addition of a column vector
     *
     * @param rowVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray subiRowVector(INDArray rowVector) {
        for(int i = 0; i < rows(); i++) {
            getRow(i).subi(rowVector.getScalar(i));
        }
        return this;
    }

    /**
     * In place addition of a column vector
     *
     * @param rowVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray subRowVector(INDArray rowVector) {
        return dup().subiRowVector(rowVector);
    }

    /**
     * In place addition of a column vector
     *
     * @param columnVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray addiColumnVector(INDArray columnVector) {
        for(int i = 0; i < columns(); i++) {
            getColumn(i).addi(columnVector.getScalar(i));
        }
        return this;
    }

    /**
     * In place addition of a column vector
     *
     * @param columnVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray addColumnVector(INDArray columnVector) {
        return dup().addiColumnVector(columnVector);
    }

    /**
     * In place addition of a column vector
     *
     * @param rowVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray addiRowVector(INDArray rowVector) {
        for(int i = 0; i < rows(); i++) {
            getRow(i).addi(rowVector.getScalar(i));
        }
        return this;
    }

    /**
     * In place addition of a column vector
     *
     * @param rowVector the column vector to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray addRowVector(INDArray rowVector) {
        return dup().addiRowVector(rowVector);
    }

    /**
     * Perform a copy matrix multiplication
     *
     * @param other the other matrix to perform matrix multiply with
     * @return the result of the matrix multiplication
     */
    @Override
    public IComplexNDArray mmul(INDArray other) {
        int[] shape = {rows(),other.columns()};
        return mmuli(other,NDArrays.create(shape));
    }

    /**
     * Perform an copy matrix multiplication
     *
     * @param other  the other matrix to perform matrix multiply with
     * @param result the result ndarray
     * @return the result of the matrix multiplication
     */
    @Override
    public IComplexNDArray mmul(INDArray other, INDArray result) {
        return dup().mmuli(other,result);
    }

    /**
     * in place (element wise) division of two matrices
     *
     * @param other the second ndarray to divide
     * @return the result of the divide
     */
    @Override
    public IComplexNDArray div(INDArray other) {
        return dup().divi(other);
    }

    /**
     * copy (element wise) division of two matrices
     *
     * @param other  the second ndarray to divide
     * @param result the result ndarray
     * @return the result of the divide
     */
    @Override
    public IComplexNDArray div(INDArray other, INDArray result) {
        return dup().divi(other,result);
    }

    /**
     * copy (element wise) multiplication of two matrices
     *
     * @param other the second ndarray to multiply
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray mul(INDArray other) {
        return dup().muli(other);
    }

    /**
     * copy (element wise) multiplication of two matrices
     *
     * @param other  the second ndarray to multiply
     * @param result the result ndarray
     * @return the result of the multiplication
     */
    @Override
    public IComplexNDArray mul(INDArray other, INDArray result) {
        return dup().muli(other,result);
    }

    /**
     * copy subtraction of two matrices
     *
     * @param other the second ndarray to subtract
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray sub(INDArray other) {
        return dup().subi(other);
    }

    /**
     * copy subtraction of two matrices
     *
     * @param other  the second ndarray to subtract
     * @param result the result ndarray
     * @return the result of the subtraction
     */
    @Override
    public IComplexNDArray sub(INDArray other, INDArray result) {
        return dup().subi(other,result);
    }

    /**
     * copy addition of two matrices
     *
     * @param other the second ndarray to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray add(INDArray other) {
        return dup().addi(other);
    }

    /**
     * copy addition of two matrices
     *
     * @param other  the second ndarray to add
     * @param result the result ndarray
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray add(INDArray other, INDArray result) {
        return dup().addi(other,result);
    }

    /**
     * Perform an copy matrix multiplication
     *
     * @param other the other matrix to perform matrix multiply with
     * @return the result of the matrix multiplication
     */
    @Override
    public IComplexNDArray mmuli(INDArray other) {
        return mmuli(other,this);
    }

    /**
     * Perform an copy matrix multiplication
     *
     * @param other  the other matrix to perform matrix multiply with
     * @param result the result ndarray
     * @return the result of the matrix multiplication
     */
    @Override
    public IComplexNDArray mmuli(INDArray other, INDArray result) {
        if (other.isScalar())
            return muli(other.getScalar(0), result);


        IComplexNDArray otherArray = NDArrays.createComplex(other);
        IComplexNDArray resultArray = NDArrays.createComplex(result);





        if (result == this || result == other) {
			/* actually, blas cannot do multiplications in-place. Therefore, we will fake by
			 * allocating a temporary object on the side and copy the result later.
			 */
            otherArray = otherArray.ravel().reshape(otherArray.shape());

            IComplexNDArray temp = NDArrays.createComplex(resultArray.shape(),ArrayUtil.calcStridesFortran(resultArray.shape()));
            NDArrays.getBlasWrapper().gemm(NDArrays.createDouble(1,0), this, otherArray, NDArrays.createDouble(0,0), temp);

            NDArrays.getBlasWrapper().copy(temp, resultArray);

        }
        else {
            otherArray = otherArray.ravel().reshape(otherArray.shape());
            IComplexNDArray thisInput =  this.ravel().reshape(shape());
            NDArrays.getBlasWrapper().gemm(NDArrays.createDouble(1, 0), thisInput, otherArray, NDArrays.createDouble(0, 0), resultArray);
        }





        return resultArray;
    }

    /**
     * in place (element wise) division of two matrices
     *
     * @param other the second ndarray to divide
     * @return the result of the divide
     */
    @Override
    public IComplexNDArray divi(INDArray other) {
        return divi(other,this);
    }

    /**
     * in place (element wise) division of two matrices
     *
     * @param other  the second ndarray to divide
     * @param result the result ndarray
     * @return the result of the divide
     */
    @Override
    public IComplexNDArray divi(INDArray other, INDArray result) {
        if(other.isScalar())
            new TwoArrayOps().from(this).scalar(other).op(DivideOp.class)
                    .to(result).build().exec();
        else
            new TwoArrayOps().from(this).other(other).op(DivideOp.class)
                    .to(result).build().exec();
        return (IComplexNDArray) result;
    }

    /**
     * in place (element wise) multiplication of two matrices
     *
     * @param other the second ndarray to multiply
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray muli(INDArray other) {
        return muli(other,this);
    }

    /**
     * in place (element wise) multiplication of two matrices
     *
     * @param other  the second ndarray to multiply
     * @param result the result ndarray
     * @return the result of the multiplication
     */
    @Override
    public IComplexNDArray muli(INDArray other, INDArray result) {
        if(other.isScalar())
            new TwoArrayOps().from(this).scalar(other).op(MultiplyOp.class)
                    .to(result).build().exec();

        else
            new TwoArrayOps().from(this).other(other).op(MultiplyOp.class)
                    .to(result).build().exec();
        return (IComplexNDArray) result;
    }

    /**
     * in place subtraction of two matrices
     *
     * @param other the second ndarray to subtract
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray subi(INDArray other) {
        return subi(other,this);
    }

    /**
     * in place subtraction of two matrices
     *
     * @param other  the second ndarray to subtract
     * @param result the result ndarray
     * @return the result of the subtraction
     */
    @Override
    public IComplexNDArray subi(INDArray other, INDArray result) {
        if(other.isScalar())
            new TwoArrayOps().from(this).scalar(other).op(SubtractOp.class)
                    .to(result).build().exec();
        else
            new TwoArrayOps().from(this).other(other).op(SubtractOp.class)
                    .to(result).build().exec();
        return (IComplexNDArray) result;
    }

    /**
     * in place addition of two matrices
     *
     * @param other the second ndarray to add
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray addi(INDArray other) {
        return addi(other,this);
    }

    /**
     * in place addition of two matrices
     *
     * @param other  the second ndarray to add
     * @param result the result ndarray
     * @return the result of the addition
     */
    @Override
    public IComplexNDArray addi(INDArray other, INDArray result) {
        if(other.isScalar())
            new TwoArrayOps().from(this).scalar(other).op(AddOp.class)
                    .to(result).build().exec();
        else
            new TwoArrayOps().from(this).other(other).op(AddOp.class)
                    .to(result).build().exec();
        return (IComplexNDArray) result;
    }



    @Override
    public IComplexNDArray get(int[] indices) {
        IComplexNDArray result = NDArrays.createComplex(data,new int[]{1,indices.length},stride,offset,ordering);

        for (int i = 0; i < indices.length; i++) {
            result.putScalar(i, get(indices[i]));
        }

        return result;
    }







    /**
     * Return transposed copy of this matrix.
     */
    @Override
    public IComplexNDArray transpose() {
        //transpose of row vector is column vector
        if(isRowVector())
            return NDArrays.createComplex(data,new int[]{shape[0],1},offset);
            //transpose of a column vector is row vector
        else if(isColumnVector())
            return NDArrays.createComplex(data,new int[]{shape[0]},offset);

        IComplexNDArray n = NDArrays.createComplex(data,reverseCopy(shape),reverseCopy(stride),offset);
        return n;

    }

    /**
     * Reshape the ndarray in to the specified dimensions,
     * possible errors being thrown for invalid shapes
     * @param shape
     * @return
     */
    @Override
    public IComplexNDArray reshape(int[] shape) {
        long ec = 1;
        for (int i = 0; i < shape.length; i++) {
            int si = shape[i];
            if (( ec * si ) != (((int) ec ) * si ))
                throw new IllegalArgumentException("Too many elements");
            ec *= shape[i];
        }
        int n= (int) ec;

        if (ec != n)
            throw new IllegalArgumentException("Too many elements");

        //vector reshapes should be c order
        if(isVector() && shape.length != this.shape.length)  {
            IComplexNDArray ndArray = NDArrays.createComplex(data,shape,NDArrays.getComplexStrides(shape,'c'),offset,ordering);
            return ndArray;
        }

        IComplexNDArray ndArray = NDArrays.createComplex(data,shape,stride,offset,ordering);
        return ndArray;

    }






    /**
     * Check whether this can be multiplied with a.
     *
     * @param a right-hand-side of the multiplication.
     * @return true iff <tt>this.columns == a.rows</tt>
     */

    public boolean multipliesWith(INDArray a) {
        return columns() == a.rows();
    }




    /**
     * Returns a copy of
     * all of the data in this array in order
     * @return all of the data in order
     */
    @Override
    public double[] data() {
        double[] ret = new double[length * 2];
        IComplexNDArray flattened = ravel();
        int count = 0;
        for(int i = 0; i < flattened.length(); i++) {
            ret[count++] = flattened.getComplex(i).realComponent().doubleValue();
            ret[count++] = flattened.getComplex(i).imaginaryComponent().doubleValue();
        }

        return ret;
    }











    /**
     * Returns the product along a given dimension
     *
     * @param dimension the dimension to getScalar the product along
     * @return the product along the specified dimension
     */
    @Override
    public IComplexNDArray prod(int dimension) {
        return NDArrays.createComplex(super.prod(dimension));

    }

    /**
     * Returns the overall mean of this ndarray
     *
     * @param dimension the dimension to getScalar the mean along
     * @return the mean along the specified dimension of this ndarray
     */
    @Override
    public IComplexNDArray mean(int dimension) {
        return NDArrays.createComplex(super.mean(dimension));

    }

    /**
     * Set the value of the ndarray to the specified value
     *
     * @param value the value to assign
     * @return the ndarray with the values
     */
    @Override
    public IComplexNDArray assign(Number value) {
        IComplexNDArray one = reshape(new int[]{1,length});
        for(int i = 0; i < one.length(); i++)
            one.put(i,NDArrays.complexScalar(value));
        return one;
    }


    /**
     * Reverse division
     *
     * @param other the matrix to divide from
     * @return
     */
    @Override
    public IComplexNDArray rdiv(INDArray other) {
        return dup().rdivi(other);
    }

    /**
     * Reverse divsion (in place)
     *
     * @param other
     * @return
     */
    @Override
    public IComplexNDArray rdivi(INDArray other) {
        return rdivi(other,this);
    }

    /**
     * Reverse division
     *
     * @param other  the matrix to subtract from
     * @param result the result ndarray
     * @return
     */
    @Override
    public IComplexNDArray rdiv(INDArray other, INDArray result) {
        return dup().rdivi(other,result);
    }

    /**
     * Reverse division (in-place)
     *
     * @param other  the other ndarray to subtract
     * @param result the result ndarray
     * @return the ndarray with the operation applied
     */
    @Override
    public IComplexNDArray rdivi(INDArray other, INDArray result) {
        return (IComplexNDArray) other.divi(this, result);
    }

    /**
     * Reverse subtraction
     *
     * @param other  the matrix to subtract from
     * @param result the result ndarray
     * @return
     */
    @Override
    public IComplexNDArray rsub(INDArray other, INDArray result) {
        return dup().rsubi(other,result);
    }

    /**
     * @param other
     * @return
     */
    @Override
    public IComplexNDArray rsub(INDArray other) {
        return dup().rsubi(other);
    }

    /**
     * @param other
     * @return
     */
    @Override
    public IComplexNDArray rsubi(INDArray other) {
        return rsubi(other,this);
    }

    /**
     * Reverse subtraction (in-place)
     *
     * @param other  the other ndarray to subtract
     * @param result the result ndarray
     * @return the ndarray with the operation applied
     */
    @Override
    public IComplexNDArray rsubi(INDArray other, INDArray result) {
        return (IComplexNDArray) other.subi(this, result);
    }


    public IComplexNumber max() {
        IComplexNDArray reshape = ravel();
        IComplexNumber max = (IComplexNumber) reshape.getScalar(0).element();

        for(int i = 1; i < reshape.length(); i++) {
            IComplexNumber curr = (IComplexNumber) reshape.getScalar(i).element();
            double val = curr.realComponent().doubleValue();
            if(val > curr.realComponent().doubleValue())
                max = curr;

        }
        return max;
    }


    public IComplexNumber min() {
        IComplexNDArray reshape = ravel();
        IComplexNumber min = (IComplexNumber) reshape.getScalar(0).element();
        for(int i = 1; i < reshape.length(); i++) {
            IComplexNumber curr = (IComplexNumber) reshape.getScalar(i).element();
            double val = curr.realComponent().doubleValue();
            if(val < curr.realComponent().doubleValue())
                min = curr;

        }
        return min;
    }

    /**
     * Returns the overall max of this ndarray
     *
     * @param dimension the dimension to getScalar the mean along
     * @return the mean along the specified dimension of this ndarray
     */
    @Override
    public IComplexNDArray max(int dimension) {
        return NDArrays.createComplex(super.max(dimension));

    }

    /**
     * Returns the overall min of this ndarray
     *
     * @param dimension the dimension to getScalar the mean along
     * @return the mean along the specified dimension of this ndarray
     */
    @Override
    public IComplexNDArray min(int dimension) {
        return NDArrays.createComplex(super.min(dimension));

    }


    /**
     * Returns the normmax along the specified dimension
     *
     * @param dimension the dimension to getScalar the norm1 along
     * @return the norm1 along the specified dimension
     */
    @Override
    public IComplexNDArray normmax(int dimension) {
        return NDArrays.createComplex(super.normmax(dimension));

    }


    /**
     * Returns the sum along the last dimension of this ndarray
     *
     * @param dimension the dimension to getScalar the sum along
     * @return the sum along the specified dimension of this ndarray
     */
    @Override
    public IComplexNDArray sum(int dimension) {
        return NDArrays.createComplex(super.sum(dimension));

    }



    /**
     * Returns the norm1 along the specified dimension
     *
     * @param dimension the dimension to getScalar the norm1 along
     * @return the norm1 along the specified dimension
     */
    @Override
    public IComplexNDArray norm1(int dimension) {
        return NDArrays.createComplex(super.norm1(dimension));

    }

    public IComplexDouble std() {
        StandardDeviation dev = new StandardDeviation();
        INDArray real = getReal();
        INDArray imag = imag();
        double std = dev.evaluate(real.data());
        double std2 = dev.evaluate(imag.data());
        return NDArrays.createDouble(std, std2);
    }

    /**
     * Standard deviation of an ndarray along a dimension
     *
     * @param dimension the dimension to get the std along
     * @return the standard deviation along a particular dimension
     */
    @Override
    public INDArray std(int dimension) {
        return NDArrays.createComplex(super.std(dimension));

    }



    /**
     * Returns the norm2 along the specified dimension
     *
     * @param dimension the dimension to getScalar the norm2 along
     * @return the norm2 along the specified dimension
     */
    @Override
    public IComplexNDArray norm2(int dimension) {
        return NDArrays.createComplex(super.norm2(dimension));
    }




    /**
     * Converts the matrix to a one-dimensional array of doubles.
     */
    @Override
    public IComplexNumber[] toArray() {
        length = ArrayUtil.prod(shape);
        IComplexNumber[] ret = new IComplexNumber[length];
        for(int i = 0; i < ret.length; i++)
            ret[i] = getComplex(i);
        return ret;
    }





    /**
     * Reshape the matrix. Number of elements must not change.
     *
     * @param newRows
     * @param newColumns
     */
    @Override
    public IComplexNDArray reshape(int newRows, int newColumns) {
        return reshape(new int[]{newRows,newColumns});
    }




    /**
     * Get the specified column
     *
     * @param c
     */
    @Override
    public IComplexNDArray getColumn(int c) {
        if(shape.length == 2) {
            int offset = this.offset + c * 2;
            IComplexNDArray ret =  NDArrays.createComplex(
                    data,
                    new int[]{shape[0], 1},
                    new int[]{stride[0], 2},
                    offset,ordering
            );
            return ret;
        }

        else
            throw new IllegalArgumentException("Unable to getFromOrigin row of non 2d matrix");

    }





    /**
     * Get a copy of a row.
     *
     * @param r
     */
    @Override
    public IComplexNDArray getRow(int r) {
        if(shape.length == 2) {
            IComplexNDArray ret =  NDArrays.createComplex(
                    data,
                    new int[]{shape[1]},
                    new int[]{stride[1]},
                    offset + (r * 2) * columns()
            );

            return ret;
        }
        else
            throw new IllegalArgumentException("Unable to getFromOrigin row of non 2d matrix");

    }

    /**
     * Compare two matrices. Returns true if and only if other is also a
     * ComplexDoubleMatrix which has the same size and the maximal absolute
     * difference in matrix elements is smaller than 1e-6.
     *
     * @param o
     */
    @Override
    public boolean equals(Object o) {
        IComplexNDArray n = null;
        if(!o.getClass().isAssignableFrom(IComplexNDArray.class))
            return false;

        if(n == null)
            n = (IComplexNDArray) o;

        //epsilon equals
        if(isScalar() && n.isScalar()) {
            IComplexNumber c = n.getComplex(0);
            return Math.abs(getComplex(0).sub(c).realComponent().doubleValue()) < 1e-6;
        }
        else if(isVector() && n.isVector()) {
            for(int i = 0; i < length; i++) {
                double curr = getComplex(i).realComponent().doubleValue();
                double comp = n.getComplex(i).realComponent().doubleValue();
                double currImag = getComplex(i).imaginaryComponent().doubleValue();
                double compImag = n.getComplex(i).imaginaryComponent().doubleValue();
                if(Math.abs(curr - comp) > 1e-6 || Math.abs(currImag - compImag) > 1e-6)
                    return false;
            }

            return true;

        }

        if(!Shape.shapeEquals(shape(),n.shape()))
            return false;
        //epsilon equals
        if(isScalar()) {
            IComplexNumber c = n.getComplex(0);
            return getComplex(0).sub(c).absoluteValue().doubleValue() < 1e-6;
        }
        else if(isVector()) {
            for(int i = 0; i < length; i++) {
                IComplexNumber curr = getComplex(i);
                IComplexNumber comp = n.getComplex(i);
                if(curr.sub(comp).absoluteValue().doubleValue() > 1e-6)
                    return false;
            }

            return true;


        }

        for (int i = 0; i< slices(); i++) {
            if (!(slice(i).equals(n.slice(i))))
                return false;
        }

        return true;


    }






    /**
     * Broadcasts this ndarray to be the specified shape
     *
     * @param shape the new shape of this ndarray
     * @return the broadcasted ndarray
     */
    @Override
    public IComplexNDArray broadcast(int[] shape) {
        return null;
    }

    /**
     * Broadcasts this ndarray to be the specified shape
     *
     * @param shape the new shape of this ndarray
     * @return the broadcasted ndarray
     */
    @Override
    public IComplexNDArray broadcasti(int[] shape) {
        return null;
    }

    /**
     * Returns a scalar (individual element)
     * of a scalar ndarray
     *
     * @return the individual item in this ndarray
     */
    @Override
    public Object element() {
        if(!isScalar())
            throw new IllegalStateException("Unable to get the element of a non scalar");
        return get(0);
    }


    /**
     * See: http://www.mathworks.com/help/matlab/ref/permute.html
     * @param rearrange the dimensions to swap to
     * @return the newly permuted array
     */
    @Override
    public IComplexNDArray permute(int[] rearrange) {
        checkArrangeArray(rearrange);
        int[] newDims = doPermuteSwap(shape,rearrange);
        int[] newStrides = doPermuteSwap(stride,rearrange);

        IComplexNDArray ret = NDArrays.createComplex(data,newDims,newStrides,offset,ordering);
        return ret;
    }





    /**
     * Flattens the array for linear indexing
     * @return the flattened version of this array
     */
    @Override
    public IComplexNDArray ravel() {
        IComplexNDArray ret = NDArrays.createComplex(new int[]{1,length});
        List<INDArray> list = new ArrayList<>();
        sliceVectors(list);
        int count = 0;
        for(int i = 0; i < list.size(); i++) {
            for(int j = 0; j < list.get(i).length(); j++)
                ret.putScalar(count++, list.get(i).get(j));
        }
        return ret;
    }



    /** Generate string representation of the matrix. */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        if (isScalar()) {
            return String.valueOf(get(0));
        }
        else if(isVector()) {
            sb.append("[ ");
            for(int i = 0; i < length; i++) {
                sb.append(get(i));
                if(i < length - 1)
                    sb.append(" ,");
            }

            sb.append("]\n");
            return sb.toString();
        }


        int length = shape[0];
        sb.append("[");
        if (length > 0) {
            sb.append(slice(0).toString());
            for (int i = 1; i < slices(); i++) {
                sb.append(slice(i).toString());
                if(i < slices() - 1)
                    sb.append(',');

            }
        }
        sb.append("]\n");
        return sb.toString();
    }




}
