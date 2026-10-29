import pytest

from app.chunking import chunk_text


def test_empty_text_returns_empty_list():
    assert chunk_text("") == []


def test_whitespace_only_text_returns_empty_list():
    assert chunk_text(" \n\t  ") == []


def test_short_text_returns_one_chunk():
    assert chunk_text("hello world", chunk_size=20, overlap=3) == ["hello world"]


def test_chunks_do_not_exceed_chunk_size():
    text = "abcdefghijklmnopqrstuvwxyz"
    chunks = chunk_text(text, chunk_size=10, overlap=2)
    assert chunks
    assert all(len(chunk) <= 10 for chunk in chunks)


def test_chunks_cover_end_of_text():
    text = "abcdefghijklmnopqrstuvwxyz"
    chunks = chunk_text(text, chunk_size=10, overlap=2)
    assert chunks[-1]
    assert chunks[-1][-1] == text[-1]


def test_overlap_is_preserved_between_adjacent_chunks():
    text = "abcdefghijklmnopqrstuvwxyz"
    chunks = chunk_text(text, chunk_size=10, overlap=3)
    assert len(chunks) > 1
    assert chunks[0][-3:] == chunks[1][:3]


@pytest.mark.parametrize("chunk_size", [0, -1, -100])
def test_non_positive_chunk_size_raises(chunk_size):
    with pytest.raises(ValueError):
        chunk_text("some text", chunk_size=chunk_size)


@pytest.mark.parametrize("overlap", [-1, 10, 11, 100])
def test_invalid_overlap_raises(overlap):
    with pytest.raises(ValueError):
        chunk_text("some text", chunk_size=10, overlap=overlap)
