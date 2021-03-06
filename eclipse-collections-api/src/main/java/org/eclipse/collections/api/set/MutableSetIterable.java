/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.set;

import java.util.Set;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.multimap.set.MutableSetIterableMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionMutableSetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * @since 6.0
 */
public interface MutableSetIterable<T> extends SetIterable<T>, MutableCollection<T>, Set<T>
{
    MutableSetIterable<T> tap(Procedure<? super T> procedure);

    MutableSetIterable<T> select(Predicate<? super T> predicate);

    <P> MutableSetIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    MutableSetIterable<T> reject(Predicate<? super T> predicate);

    <P> MutableSetIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    PartitionMutableSetIterable<T> partition(Predicate<? super T> predicate);

    <P> PartitionMutableSetIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    <S> MutableSetIterable<S> selectInstancesOf(Class<S> clazz);

    <V> MutableSetIterableMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    <V> MutableSetIterableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Deprecated
    <S> MutableCollection<Pair<T, S>> zip(Iterable<S> that);

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Deprecated
    MutableSetIterable<Pair<T, Integer>> zipWithIndex();
}
