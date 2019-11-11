﻿using System;

namespace TrainKata.Options
{
    public class None<T> : Maybe<T>
    {
        public Maybe<TResult> Map<TResult>(Func<T, Maybe<TResult>> apply)
        {
            return new None<TResult>();
        }

        public T OrDefault(T defaultValue)
        {
            return defaultValue;
        }
    }
}