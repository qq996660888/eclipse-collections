/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.sorted.PartitionMutableSortedSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.ParallelSortedSetIterable;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.procedure.CollectIfProcedure;
import org.eclipse.collections.impl.block.procedure.CollectProcedure;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.block.procedure.FlatCollectProcedure;
import org.eclipse.collections.impl.block.procedure.PartitionPredicate2Procedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.RejectProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.block.procedure.SelectProcedure;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectBooleanProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectByteProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectCharProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectDoubleProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectFloatProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectIntProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectLongProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectShortProcedure;
import org.eclipse.collections.impl.collection.mutable.AbstractMutableCollection;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.lazy.parallel.set.sorted.NonParallelSortedSetIterable;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.eclipse.collections.impl.multimap.set.sorted.TreeSortedSetMultimap;
import org.eclipse.collections.impl.partition.set.sorted.PartitionTreeSortedSet;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.eclipse.collections.impl.utility.OrderedIterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;
import org.eclipse.collections.impl.utility.internal.SetIterables;
import org.eclipse.collections.impl.utility.internal.SetIterate;
import org.eclipse.collections.impl.utility.internal.SortedSetIterables;

public class TreeSortedSet<T> extends AbstractMutableCollection<T>
        implements Externalizable, MutableSortedSet<T>
{
    private static final long serialVersionUID = 1L;
    private TreeSet<T> treeSet;

    public TreeSortedSet()
    {
        this.treeSet = new TreeSet<T>();
    }

    public TreeSortedSet(Iterable<? extends T> iterable)
    {
        this.treeSet = new TreeSet<T>();
        this.addAllIterable(iterable);
    }

    public TreeSortedSet(Comparator<? super T> comparator)
    {
        this.treeSet = new TreeSet<T>(comparator);
    }

    public TreeSortedSet(SortedSet<T> set)
    {
        this.treeSet = new TreeSet<T>(set);
    }

    public TreeSortedSet(Comparator<? super T> comparator, Iterable<? extends T> iterable)
    {
        this(comparator);
        this.addAllIterable(iterable);
    }

    public static <T> TreeSortedSet<T> newSet()
    {
        return new TreeSortedSet<T>();
    }

    public static <T> TreeSortedSet<T> newSet(Comparator<? super T> comparator)
    {
        return new TreeSortedSet<T>(comparator);
    }

    public static <T> TreeSortedSet<T> newSet(Iterable<? extends T> source)
    {
        if (source instanceof SortedSet<?>)
        {
            return new TreeSortedSet<T>((SortedSet<T>) source);
        }
        TreeSortedSet<T> sortedSet = TreeSortedSet.newSet();
        Iterate.forEach(source, CollectionAddProcedure.on(sortedSet));
        return sortedSet;
    }

    public static <T> TreeSortedSet<T> newSet(Comparator<? super T> comparator, Iterable<? extends T> iterable)
    {
        return new TreeSortedSet<T>(comparator, iterable);
    }

    public static <T> TreeSortedSet<T> newSetWith(T... elements)
    {
        return new TreeSortedSet<T>().with(elements);
    }

    public static <T> TreeSortedSet<T> newSetWith(Comparator<? super T> comparator, T... elements)
    {
        return new TreeSortedSet<T>(comparator).with(elements);
    }

    public MutableSortedSet<T> asUnmodifiable()
    {
        return UnmodifiableSortedSet.of(this);
    }

    public MutableSortedSet<T> asSynchronized()
    {
        return SynchronizedSortedSet.of(this);
    }

    @Beta
    public ParallelSortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        if (executorService == null)
        {
            throw new NullPointerException();
        }
        if (batchSize < 1)
        {
            throw new IllegalArgumentException();
        }
        return new NonParallelSortedSetIterable<T>(this);
    }

    public ImmutableSortedSet<T> toImmutable()
    {
        return SortedSets.immutable.withSortedSet(this);
    }

    public MutableStack<T> toStack()
    {
        return ArrayStack.newStack(this);
    }

    @Override
    public boolean add(T element)
    {
        return this.treeSet.add(element);
    }

    @Override
    public boolean remove(Object element)
    {
        return this.treeSet.remove(element);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        return this.treeSet.addAll(collection);
    }

    @Override
    public boolean contains(Object o)
    {
        return this.treeSet.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return Iterate.allSatisfyWith(collection, Predicates2.in(), this.treeSet);
    }

    public void clear()
    {
        this.treeSet.clear();
    }

    @Override
    public TreeSortedSet<T> clone()
    {
        try
        {
            TreeSortedSet<T> clone = (TreeSortedSet<T>) super.clone();
            clone.treeSet = (TreeSet<T>) this.treeSet.clone();
            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            throw new AssertionError(e);
        }
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (!(object instanceof Set))
        {
            return false;
        }

        Set<?> other = (Set<?>) object;
        return other.equals(this.treeSet);
    }

    @Override
    public int hashCode()
    {
        return this.treeSet.hashCode();
    }

    public TreeSortedSet<T> with(T element)
    {
        this.treeSet.add(element);
        return this;
    }

    public TreeSortedSet<T> with(T element1, T element2)
    {
        this.treeSet.add(element1);
        this.treeSet.add(element2);
        return this;
    }

    public TreeSortedSet<T> with(T element1, T element2, T element3)
    {
        this.treeSet.add(element1);
        this.treeSet.add(element2);
        this.treeSet.add(element3);
        return this;
    }

    public TreeSortedSet<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, CollectionAddProcedure.on(this.treeSet));
        return this;
    }

    public TreeSortedSet<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    public TreeSortedSet<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    public TreeSortedSet<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    public TreeSortedSet<T> newEmpty()
    {
        return TreeSortedSet.newSet(this.treeSet.comparator());
    }

    public TreeSortedSet<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    public void each(Procedure<? super T> procedure)
    {
        IterableIterate.forEach(this, procedure);
    }

    public TreeSortedSet<T> select(Predicate<? super T> predicate)
    {
        TreeSortedSet<T> result = this.newEmpty();
        this.forEach(new SelectProcedure<T>(predicate, result));
        return result;
    }

    public TreeSortedSet<T> reject(Predicate<? super T> predicate)
    {
        TreeSortedSet<T> result = this.newEmpty();
        this.forEach(new RejectProcedure<T>(predicate, result));
        return result;
    }

    public PartitionMutableSortedSet<T> partition(Predicate<? super T> predicate)
    {
        PartitionTreeSortedSet<T> partitionTreeSortedSet = new PartitionTreeSortedSet<T>(this.comparator());
        this.forEach(new PartitionProcedure<T>(predicate, partitionTreeSortedSet));
        return partitionTreeSortedSet;
    }

    public <P> PartitionMutableSortedSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionTreeSortedSet<T> partitionTreeSortedSet = new PartitionTreeSortedSet<T>(this.comparator());
        this.forEach(new PartitionPredicate2Procedure<T, P>(predicate, parameter, partitionTreeSortedSet));
        return partitionTreeSortedSet;
    }

    public PartitionMutableSortedSet<T> partitionWhile(Predicate<? super T> predicate)
    {
        PartitionTreeSortedSet<T> result = new PartitionTreeSortedSet<T>(this.comparator());
        return IterableIterate.partitionWhile(this, predicate, result);
    }

    public <S> TreeSortedSet<S> selectInstancesOf(Class<S> clazz)
    {
        TreeSortedSet<S> result = (TreeSortedSet<S>) this.newEmpty();
        this.forEach(new SelectInstancesOfProcedure<S>(clazz, result));
        return result;
    }

    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        MutableList<V> result = FastList.newList();
        this.forEach(new CollectProcedure<T, V>(function, result));
        return result;
    }

    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        BooleanArrayList result = new BooleanArrayList(this.size());
        this.forEach(new CollectBooleanProcedure<T>(booleanFunction, result));
        return result;
    }

    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        ByteArrayList result = new ByteArrayList(this.size());
        this.forEach(new CollectByteProcedure<T>(byteFunction, result));
        return result;
    }

    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        CharArrayList result = new CharArrayList(this.size());
        this.forEach(new CollectCharProcedure<T>(charFunction, result));
        return result;
    }

    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        DoubleArrayList result = new DoubleArrayList(this.size());
        this.forEach(new CollectDoubleProcedure<T>(doubleFunction, result));
        return result;
    }

    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        FloatArrayList result = new FloatArrayList(this.size());
        this.forEach(new CollectFloatProcedure<T>(floatFunction, result));
        return result;
    }

    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        IntArrayList result = new IntArrayList(this.size());
        this.forEach(new CollectIntProcedure<T>(intFunction, result));
        return result;
    }

    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        LongArrayList result = new LongArrayList(this.size());
        this.forEach(new CollectLongProcedure<T>(longFunction, result));
        return result;
    }

    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        ShortArrayList result = new ShortArrayList(this.size());
        this.forEach(new CollectShortProcedure<T>(shortFunction, result));
        return result;
    }

    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        MutableList<V> result = FastList.newList();
        this.forEach(new FlatCollectProcedure<T, V>(function, result));
        return result;
    }

    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        MutableList<V> result = FastList.newList();
        this.forEach(new CollectIfProcedure<T, V>(result, function, predicate));
        return result;
    }

    public int detectIndex(Predicate<? super T> predicate)
    {
        return Iterate.detectIndex(this, predicate);
    }

    public <V> TreeSortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return Iterate.groupBy(this.treeSet, function, TreeSortedSetMultimap.<V, T>newMultimap(this.comparator()));
    }

    public <V> TreeSortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return Iterate.groupByEach(this.treeSet, function, TreeSortedSetMultimap.<V, T>newMultimap(this.comparator()));
    }

    public <P> TreeSortedSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.selectWith(this.treeSet, predicate, parameter, this.newEmpty());
    }

    public <P> TreeSortedSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return Iterate.rejectWith(this.treeSet, predicate, parameter, this.newEmpty());
    }

    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return Iterate.collectWith(this.treeSet, function, parameter, FastList.<V>newList());
    }

    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return Iterate.zip(this, that, FastList.<Pair<T, S>>newList());
    }

    public TreeSortedSet<Pair<T, Integer>> zipWithIndex()
    {
        Comparator<? super T> comparator = this.comparator();
        if (comparator == null)
        {
            TreeSortedSet<Pair<T, Integer>> pairs = TreeSortedSet.newSet(Comparators.<Pair<T, Integer>, T>byFunction(Functions.<T>firstOfPair(), Comparators.<T>naturalOrder()));
            return Iterate.zipWithIndex(this, pairs);
        }
        return Iterate.zipWithIndex(this, TreeSortedSet.<Pair<T, Integer>>newSet(Comparators.byFirstOfPair(comparator)));
    }

    public MutableSortedSet<T> takeWhile(Predicate<? super T> predicate)
    {
        MutableSortedSet<T> result = TreeSortedSet.newSet(this.comparator());
        return IterableIterate.takeWhile(this, predicate, result);
    }

    public MutableSortedSet<T> dropWhile(Predicate<? super T> predicate)
    {
        MutableSortedSet<T> result = TreeSortedSet.newSet(this.comparator());
        return IterableIterate.dropWhile(this, predicate, result);
    }

    public MutableSortedSet<T> distinct()
    {
        return this.clone();
    }

    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        return OrderedIterate.corresponds(this, other, predicate);
    }

    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size());

        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }

        Iterator<T> iterator = this.iterator();
        int i = 0;
        while (iterator.hasNext() && i <= toIndex)
        {
            T each = iterator.next();
            if (i >= fromIndex)
            {
                procedure.value(each);
            }
            i++;
        }
    }

    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(fromIndex, toIndex, this.size());

        if (fromIndex > toIndex)
        {
            throw new IllegalArgumentException("fromIndex must not be greater than toIndex");
        }

        Iterator<T> iterator = this.iterator();
        int i = 0;
        while (iterator.hasNext() && i <= toIndex)
        {
            T each = iterator.next();
            if (i >= fromIndex)
            {
                objectIntProcedure.value(each, i);
            }
            i++;
        }
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        return SetIterate.removeAllIterable(this.treeSet, iterable);
    }

    public Comparator<? super T> comparator()
    {
        return this.treeSet.comparator();
    }

    public MutableSortedSet<T> subSet(T fromElement, T toElement)
    {
        return SortedSetAdapter.adapt(this.treeSet.subSet(fromElement, toElement));
    }

    public MutableSortedSet<T> headSet(T toElement)
    {
        return SortedSetAdapter.adapt(this.treeSet.headSet(toElement));
    }

    public MutableSortedSet<T> tailSet(T fromElement)
    {
        return SortedSetAdapter.adapt(this.treeSet.tailSet(fromElement));
    }

    public T first()
    {
        return this.treeSet.first();
    }

    public T last()
    {
        return this.treeSet.last();
    }

    public int indexOf(Object object)
    {
        if (this.treeSet.contains(object))
        {
            return this.treeSet.headSet((T) object).size();
        }
        return -1;
    }

    public T getFirst()
    {
        return this.first();
    }

    public T getLast()
    {
        return this.last();
    }

    public MutableSortedSet<SortedSetIterable<T>> powerSet()
    {
        return (MutableSortedSet<SortedSetIterable<T>>) (MutableSortedSet<?>) SortedSetIterables.powerSet(this);
    }

    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        return SetIterables.cartesianProduct(this, set);
    }

    public TreeSortedSet<T> union(SetIterable<? extends T> set)
    {
        return SetIterables.unionInto(this, set, this.newEmpty());
    }

    public TreeSortedSet<T> intersect(SetIterable<? extends T> set)
    {
        return SetIterables.intersectInto(this, set, this.newEmpty());
    }

    public TreeSortedSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        return SetIterables.differenceInto(this, subtrahendSet, this.newEmpty());
    }

    public MutableSortedSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        return SetIterables.symmetricDifferenceInto(this, setB, this.newEmpty());
    }

    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.unionInto(this, set, targetSet);
    }

    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.intersectInto(this, set, targetSet);
    }

    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        return SetIterables.differenceInto(this, subtrahendSet, targetSet);
    }

    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.symmetricDifferenceInto(this, set, targetSet);
    }

    public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return SetIterables.isSubsetOf(this, candidateSuperset);
    }

    public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return SetIterables.isProperSubsetOf(this, candidateSuperset);
    }

    public Iterator<T> iterator()
    {
        return this.treeSet.iterator();
    }

    public int size()
    {
        return this.treeSet.size();
    }

    public MutableSortedSet<T> toReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toReversed() not implemented yet");
    }

    public MutableSortedSet<T> take(int count)
    {
        return IterableIterate.take(this, Math.min(this.size(), count), this.newEmpty());
    }

    public MutableSortedSet<T> drop(int count)
    {
        return IterableIterate.drop(this, count, this.newEmpty());
    }

    public void reverseForEach(Procedure<? super T> procedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".reverseForEach() not implemented yet");
    }

    public LazyIterable<T> asReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asReversed() not implemented yet");
    }

    public int detectLastIndex(Predicate<? super T> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectLastIndex() not implemented yet");
    }

    public void writeExternal(final ObjectOutput out) throws IOException
    {
        out.writeObject(this.comparator());
        out.writeInt(this.size());
        this.forEach(new CheckedProcedure<T>()
        {
            public void safeValue(T each) throws Exception
            {
                out.writeObject(each);
            }
        });
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.treeSet = new TreeSet<T>((Comparator<T>) in.readObject());
        int size = in.readInt();
        for (int i = 0; i < size; i++)
        {
            this.add((T) in.readObject());
        }
    }

    public int compareTo(SortedSetIterable<T> otherSet)
    {
        return SortedSetIterables.compare(this, otherSet);
    }
}
