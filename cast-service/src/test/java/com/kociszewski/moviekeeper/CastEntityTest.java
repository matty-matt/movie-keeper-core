//package com.kociszewski.castservice;
//
//import com.kociszewski.castservice.domain.commands.FindCastCommand;
//import com.kociszewski.castservice.domain.commands.SaveCastCommand;
//import com.kociszewski.castservice.domain.events.CastFoundEvent;
//import com.kociszewski.castservice.domain.events.CastSavedEvent;
//import com.kociszewski.movieservice.domain.MovieAggregate;
//import com.kociszewski.movieservice.domain.events.MovieSearchDelegatedEvent;
//import com.kociszewski.movieservice.infrastructure.cast.CastDTO;
//import com.kociszewski.movieservice.infrastructure.cast.CastInfoDTO;
//import org.axonframework.test.aggregate.AggregateTestFixture;
//import org.axonframework.test.aggregate.FixtureConfiguration;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Collections;
//import java.util.UUID;
//
//public class CastEntityTest {
//
//    private FixtureConfiguration<MovieAggregate> fixture;
//    private MovieId movieId;
//    private TrailerEntityId trailerEntityId;
//    private CastEntityId castEntityId;
//    private SearchPhrase searchPhrase;
//    private CastDTO castDTO;
//    private ExternalMovieId externalMovieId;
//
//    @BeforeEach
//    public void setup() {
//        this.fixture = new AggregateTestFixture<>(MovieAggregate.class);
//        this.movieId = new MovieId(UUID.randomUUID().toString());
//        this.externalMovieId = new ExternalMovieId("123");
//        this.castDTO = CastDTO.builder()
//                .aggregateId(UUID.randomUUID().toString())
//                .cast(Collections.singletonList(CastInfoDTO.builder()
//                        .id("678")
//                        .castId("568")
//                        .character("John")
//                        .name("Elon Musk")
//                        .gender((short)2)
//                        .order(1)
//                        .profilePath("/elon.jpg")
//                        .build()))
//                .movieId(movieId.getId())
//                .externalMovieId(externalMovieId.getId())
//                .build();
//
//        this.trailerEntityId = new TrailerEntityId(UUID.randomUUID().toString());
//        this.castEntityId = new CastEntityId(UUID.randomUUID().toString());
//        this.searchPhrase = new SearchPhrase("some title");
//    }
//
//    @Test
//    public void shouldCastFoundEventAppear() {
//        fixture.given(
//                new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase))
//                .when(new FindCastCommand(movieId, externalMovieId))
//                .expectEvents(new CastFoundEvent(movieId, castEntityId, externalMovieId));
//    }
//
//    @Test
//    public void shouldCastSavedEventAppear() {
//        fixture.given(
//                new MovieSearchDelegatedEvent(movieId, trailerEntityId, castEntityId, searchPhrase))
//                .when(new SaveCastCommand(movieId, castDTO))
//                .expectEvents(new CastSavedEvent(castDTO))
//                .expectState(state -> {
//                    var cast = state.getCastEntity().getCast();
//                    assertThat(cast.size()).isEqualTo(1);
//                    var actor = cast.get(0);
//                    assertThat(actor.getCastId()).isEqualTo("568");
//                    assertThat(actor.getId()).isEqualTo("678");
//                    assertThat(actor.getCharacter()).isEqualTo("John");
//                    assertThat(actor.getName()).isEqualTo("Elon Musk");
//                    assertThat(actor.getGender()).isEqualTo((short)2);
//                    assertThat(actor.getOrder()).isEqualTo(1);
//                    assertThat(actor.getProfilePath()).isEqualTo("/elon.jpg");
//                });
//    }
//}
