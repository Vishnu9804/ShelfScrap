import { useEffect, useState } from "react";
import BookCard from "./BookCard";
import React from "react";
import axios from "axios";

const MySuggestions = () => {
    const [books, setBooks] = useState([]);
    const [loggedInUser, setLoggedInUser] = useState(null);

    useEffect(() => {
        const storedUsername = localStorage.getItem("username");
        if (storedUsername) {
            setLoggedInUser(storedUsername);
        }
    }, []);

    useEffect(() => {
        console.log("User Name :- ", loggedInUser);
    }, [loggedInUser]);

    useEffect(() => {
        if (loggedInUser) {
            console.log("Logged-in User:", loggedInUser);

            // Fetch recommendations.json
            fetch('/recommendations.json')
                .then(response => response.json())
                .then(async (data) => {
                    console.log("Fetched Data:", data);

                    // Filter books only for the logged-in user
                    const bookIds = data
                        .filter(book => book.username === loggedInUser)
                        .map(book => book.recommended_book);

                    console.log("Filtered Book IDs:", bookIds);

                    // Fetch book details using the book_id
                    const bookDetailsPromises = bookIds.map(book_id =>
                        axios.get(`http://localhost:8080/books/${book_id}`)
                            .then(response => response.data)
                            .catch(error => {
                                console.error(`Error fetching book with ID ${book_id}:`, error);
                                return null; // Handle errors gracefully
                            })
                    );

                    // Wait for all API calls to resolve
                    const bookDetails = await Promise.all(bookDetailsPromises);

                    // Filter out failed requests (null values)
                    const validBooks = bookDetails.filter(book => book !== null);
                    setBooks(validBooks);

                    console.log("Fetched Book Objects:", validBooks);
                })
                .catch(error => console.error("Error fetching recommendations:", error));
        }
    }, [loggedInUser]);

    return (
        <div className='bg-[url("https://cdn.cbeditz.com/cbeditz/large/dark-brown-wood-wooden-background-free-wallpaper-zdu3w.jpg")]'>
            <h2 className='text-4xl text-center font-sans sticky top-0 z-10 p-4 bg-opacity-80 bg-gray-900 text-white'>
                Suggested Books
            </h2>
            <div className="w-fit mx-auto grid grid-cols-1 lg:grid-cols-3 md:grid-cols-2 justify-items-center justify-center gap-y-20 gap-x-14 mt-10 mb-5">
                {books.map(book => (
                    <BookCard key={book.book_id} book={book} />
                ))}
            </div>
        </div>
    );
};

export default MySuggestions;
