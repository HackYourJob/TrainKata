using System;
using System.Collections.Generic;
using KataTrainReservation;
using Xunit;

namespace TrainKata.Tests
{
    public class TicketOfficeTest
    {
        private static readonly string TrainId = "9043-2018-05-24";
        private static readonly string BookingReference = "75bcd15";

        [Fact]
        public void Reserve_seats_when_train_is_empty()
        {
            const int seatsRequestedCount = 3;
            var service = BuildTicketOfficeService(TrainTopologies.With_10_available_seats());

            var reservation = service.MakeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

            Assert.Equal("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"" + BookingReference + "\", \"seats\": [\"1A\", \"2A\", \"3A\"]}", reservation);
        }

        [Fact]
        public void Not_reserve_seats_when_not_enough_free_place()
        {
            const int seatsRequestedCount = 5;
            var trainDataService = BuildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved());

            var reservation = trainDataService.MakeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

            Assert.Equal("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
        }

        [Fact]
        public void Reserve_seats_when_one_coach_is_full_and_one_is_empty()
        {
            const int seatsRequestedCount = 3;
            var trainDataService = BuildTicketOfficeService(TrainTopologies.With_2_coaches_and_the_first_coach_is_full());

            var reservation = trainDataService.MakeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

            Assert.Equal("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"" + BookingReference + "\", \"seats\": [\"1B\", \"2B\", \"3B\"]}", reservation);
        }

        [Fact]
        public void Reserve_all_seats_in_the_same_coach()
        {
            const int seatsRequestedCount = 2;
            var trainDataService = BuildTicketOfficeService(TrainTopologies.With_2_coaches_and_9_seats_already_reserved_in_the_first_coach());

            var reservation = trainDataService.MakeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

            Assert.Equal("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"" + BookingReference + "\", \"seats\": [\"1B\", \"2B\"]}", reservation);
        }

        [Fact]
        public void Cannot_Reserve_When_Train_Is_Not_Full_But_Not_Coach_Is_Available()
        {
            const int seatsRequestedCount = 2;
            var trainDataService = BuildTicketOfficeService(TrainTopologies.With_10_coaches_half_available());

            var reservation = trainDataService.MakeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

            Assert.Equal("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
        }

        [Fact(Skip = "New feature not implemented")]
        public void Not_reserve_seats_when_it_exceed_max_capacity_threshold()
        {
            const int seatsRequestedCount = 3;
            var service = BuildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved());

            var reservation = service.MakeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

            Assert.Equal("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
        }

        private static TicketOfficeService BuildTicketOfficeService(String topologies)
        {
            return new TicketOfficeService(new TrainDataClientStub(topologies), new BookingReferenceClientStub(BookingReference));
        }

        private class BookingReferenceClientStub : IBookingReferenceClient
        {
            private readonly string _bookingReference;

            public BookingReferenceClientStub(string bookingReference)
            {
                _bookingReference = bookingReference;
            }

            public string GenerateBookingReference()
            {
                return _bookingReference;
            }

            public void BookTrain(string trainId, string bookingReference, List<Seat> seats)
            {
            }
        }

        private class TrainDataClientStub : ITrainDataClient
        {
            private readonly string _topologies;

            public TrainDataClientStub(string topologies)
            {
                _topologies = topologies;
            }

            public string GetTopology(string trainId)
            {
                return _topologies;
            }
        }
    }
}