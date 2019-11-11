using System;

namespace TrainKata.Domain.Options
{
    public class Some<T> : Maybe<T>
    {
        public Some(T value)
        {
            Value = value;
        }

        public T Value { get; }

        public Maybe<TResult> Map<TResult>(Func<T, Maybe<TResult>> apply)
        {
            return apply(Value);
        }

        public T OrDefault(T defaultValue)
        {
            return Value;
        }
    }
}