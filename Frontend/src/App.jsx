import { ThemeProvider } from "@emotion/react";
import "./App.css";
import Routers from "./routers/Routers";
import { darkTheme } from "./theme/darkTheme";
import { CssBaseline } from "@mui/material";
import { GenreProvider } from "./customers/context/genreContext";
import React from "react";
function App() {
  return (
    <ThemeProvider theme={darkTheme}>
      <GenreProvider>
        <CssBaseline />
        <Routers />
      </GenreProvider>
    </ThemeProvider>
  );
}

export default App;
