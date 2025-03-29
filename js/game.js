class Game2048 {
    constructor() {
        this.gridSize = 4;
        this.grid = Array(this.gridSize).fill().map(() => Array(this.gridSize).fill(0));
        this.score = 0;
        this.bestScore = parseInt(localStorage.getItem('bestScore')) || 0;
        this.gameOver = false;
        this.init();
    }

    init() {
        this.createGrid();
        this.addNewTile();
        this.addNewTile();
        this.updateDisplay();
        this.setupEventListeners();
    }

    createGrid() {
        const container = document.getElementById('grid-container');
        container.innerHTML = '';
        
        for (let i = 0; i < this.gridSize * this.gridSize; i++) {
            const cell = document.createElement('div');
            cell.classList.add('grid-cell');
            container.appendChild(cell);
        }
    }

    updateDisplay() {
        const cells = document.querySelectorAll('.grid-cell');
        let cellIndex = 0;
        
        for (let i = 0; i < this.gridSize; i++) {
            for (let j = 0; j < this.gridSize; j++) {
                const value = this.grid[i][j];
                const cell = cells[cellIndex];
                cell.textContent = value || '';
                cell.className = 'grid-cell' + (value ? ` tile-${value}` : '');
                cellIndex++;
            }
        }

        document.getElementById('score').textContent = this.score;
        document.getElementById('best-score').textContent = this.bestScore;
    }

    addNewTile() {
        const emptyCells = [];
        for (let i = 0; i < this.gridSize; i++) {
            for (let j = 0; j < this.gridSize; j++) {
                if (this.grid[i][j] === 0) {
                    emptyCells.push({x: i, y: j});
                }
            }
        }

        if (emptyCells.length > 0) {
            const randomCell = emptyCells[Math.floor(Math.random() * emptyCells.length)];
            this.grid[randomCell.x][randomCell.y] = Math.random() < 0.9 ? 2 : 4;
        }
    }

    move(direction) {
        if (this.gameOver) return;

        const originalGrid = JSON.stringify(this.grid);
        let moved = false;

        switch(direction) {
            case 'ArrowLeft':
                moved = this.moveLeft();
                break;
            case 'ArrowRight':
                moved = this.moveRight();
                break;
            case 'ArrowUp':
                moved = this.moveUp();
                break;
            case 'ArrowDown':
                moved = this.moveDown();
                break;
        }

        if (moved) {
            this.addNewTile();
            this.updateDisplay();
            
            if (this.score > this.bestScore) {
                this.bestScore = this.score;
                localStorage.setItem('bestScore', this.bestScore);
            }

            if (this.isGameOver()) {
                this.gameOver = true;
                document.getElementById('game-over').style.display = 'block';
                document.getElementById('final-score').textContent = this.score;
            }
        }
    }

    moveLeft() {
        return this.moveHorizontal('left');
    }

    moveRight() {
        return this.moveHorizontal('right');
    }

    moveUp() {
        return this.moveVertical('up');
    }

    moveDown() {
        return this.moveVertical('down');
    }

    moveHorizontal(direction) {
        let moved = false;
        for (let i = 0; i < this.gridSize; i++) {
            const row = this.grid[i].filter(cell => cell !== 0);
            if (direction === 'right') row.reverse();

            // Merge tiles
            for (let j = 0; j < row.length - 1; j++) {
                if (row[j] === row[j + 1]) {
                    row[j] *= 2;
                    this.score += row[j];
                    row.splice(j + 1, 1);
                    moved = true;
                }
            }

            // Fill with zeros
            while (row.length < this.gridSize) {
                direction === 'left' ? row.push(0) : row.unshift(0);
            }

            if (direction === 'right') row.reverse();
            
            // Check if the row has changed
            if (JSON.stringify(this.grid[i]) !== JSON.stringify(row)) {
                moved = true;
            }
            this.grid[i] = row;
        }
        return moved;
    }

    moveVertical(direction) {
        let moved = false;
        for (let j = 0; j < this.gridSize; j++) {
            let column = [];
            for (let i = 0; i < this.gridSize; i++) {
                column.push(this.grid[i][j]);
            }
            
            column = column.filter(cell => cell !== 0);
            if (direction === 'down') column.reverse();

            // Merge tiles
            for (let i = 0; i < column.length - 1; i++) {
                if (column[i] === column[i + 1]) {
                    column[i] *= 2;
                    this.score += column[i];
                    column.splice(i + 1, 1);
                    moved = true;
                }
            }

            // Fill with zeros
            while (column.length < this.gridSize) {
                direction === 'up' ? column.push(0) : column.unshift(0);
            }

            if (direction === 'down') column.reverse();

            // Update the grid and check if it changed
            for (let i = 0; i < this.gridSize; i++) {
                if (this.grid[i][j] !== column[i]) {
                    moved = true;
                }
                this.grid[i][j] = column[i];
            }
        }
        return moved;
    }

    isGameOver() {
        // Check for empty cells
        for (let i = 0; i < this.gridSize; i++) {
            for (let j = 0; j < this.gridSize; j++) {
                if (this.grid[i][j] === 0) return false;
            }
        }

        // Check for possible merges
        for (let i = 0; i < this.gridSize; i++) {
            for (let j = 0; j < this.gridSize; j++) {
                const current = this.grid[i][j];
                // Check right neighbor
                if (j < this.gridSize - 1 && current === this.grid[i][j + 1]) return false;
                // Check bottom neighbor
                if (i < this.gridSize - 1 && current === this.grid[i + 1][j]) return false;
            }
        }

        return true;
    }

    setupEventListeners() {
        document.addEventListener('keydown', (e) => {
            if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
                e.preventDefault();
                this.move(e.key);
            }
        });

        document.getElementById('new-game').addEventListener('click', () => {
            this.resetGame();
        });

        // Add touch support
        let touchStartX = 0;
        let touchStartY = 0;
        
        document.addEventListener('touchstart', (e) => {
            touchStartX = e.touches[0].clientX;
            touchStartY = e.touches[0].clientY;
        });

        document.addEventListener('touchend', (e) => {
            const touchEndX = e.changedTouches[0].clientX;
            const touchEndY = e.changedTouches[0].clientY;
            
            const deltaX = touchEndX - touchStartX;
            const deltaY = touchEndY - touchStartY;
            
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (deltaX > 0) {
                    this.move('ArrowRight');
                } else {
                    this.move('ArrowLeft');
                }
            } else {
                if (deltaY > 0) {
                    this.move('ArrowDown');
                } else {
                    this.move('ArrowUp');
                }
            }
        });
    }

    resetGame() {
        this.grid = Array(this.gridSize).fill().map(() => Array(this.gridSize).fill(0));
        this.score = 0;
        this.gameOver = false;
        document.getElementById('game-over').style.display = 'none';
        this.addNewTile();
        this.addNewTile();
        this.updateDisplay();
    }
}

// Start the game when the page loads
window.addEventListener('DOMContentLoaded', () => {
    new Game2048();
});