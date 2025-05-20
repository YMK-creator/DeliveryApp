import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import {
    Box,
    Drawer,
    List,
    ListItem,
    Button,
    AppBar,
    Toolbar,
    IconButton,
    Paper,
    Typography,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';

import HomePage from './pages/HomePage';
import FoodPage from './pages/FoodPage';
import CategoryPage from './pages/CategoryPage';
import IngredientPage from './pages/IngredientPage';

const drawerWidth = 240;

const App: React.FC = () => {
    const [drawerOpen, setDrawerOpen] = useState(false);

    const toggleDrawer = () => {
        setDrawerOpen(prev => !prev);
    };

    return (
        <Router>
            <Box sx={{ display: 'flex' }}>
                {/* Top App Bar */}
                <AppBar position="fixed" sx={{ zIndex: theme => theme.zIndex.drawer + 1 }}>
                    <Toolbar>
                        <IconButton
                            color="inherit"
                            edge="start"
                            onClick={toggleDrawer}
                            sx={{ mr: 2 }}
                        >
                            <MenuIcon />
                        </IconButton>
                        <Paper elevation={3} sx={{ padding: '0.3rem 1rem', borderRadius: '8px' }}>
                            <Typography variant="h6" component="div">
                                🍔 Доставка еды
                            </Typography>
                        </Paper>
                    </Toolbar>
                </AppBar>

                {/* Side Drawer */}
                <Drawer
                    variant="persistent"
                    open={drawerOpen}
                    sx={{
                        width: drawerWidth,
                        flexShrink: 0,
                        '& .MuiDrawer-paper': {
                            width: drawerWidth,
                            boxSizing: 'border-box',
                            transition: 'width 0.3s',
                        },
                    }}
                >
                    <Toolbar />
                    <List>
                        <ListItem>
                            <Button component={Link} to="/">🏠 Домашняя</Button>
                        </ListItem>
                        <ListItem>
                            <Button component={Link} to="/food">🍔 Блюда</Button>
                        </ListItem>
                        <ListItem>
                            <Button component={Link} to="/categories">📁 Категории</Button>
                        </ListItem>
                        <ListItem>
                            <Button component={Link} to="/ingredients">🧂 Ингредиенты</Button>
                        </ListItem>
                    </List>
                </Drawer>

                {/* Main Content */}
                <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                    <Toolbar />
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/food" element={<FoodPage />} />
                        <Route path="/categories" element={<CategoryPage />} />
                        <Route path="/ingredients" element={<IngredientPage />} />
                        <Route path="*" element={<HomePage />} />
                    </Routes>
                </Box>
            </Box>
        </Router>
    );
};

export default App;
