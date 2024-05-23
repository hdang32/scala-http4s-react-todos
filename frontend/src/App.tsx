import { useState } from "react";
import "./App.css";

const defaultItems = [
  { id: 1, name: "Eat" },
  { id: 2, name: "Sleep" },
];


function App() {
  const [items, setItems] = useState(defaultItems);
  const [curr, setCurr] = useState("");
  const deleteHandler = (id: number) => setItems(items.filter(item=>item.id != id));

  const renderedItems = items.map((item) => <ListItem item={item} onDelete={deleteHandler} />);

  return (
    <>
      <ul>{renderedItems}</ul>
      <input
        onChange={(event) => setCurr(event.target.value)}
        value={curr}
      ></input>
      <button
        onClick={() => {
          setItems([
            ...items,
            {
              id: Math.random() * 1000,
              name: curr,
            },
          ]);
          setCurr("");
        }}
      >
        {" "}
        Add{" "}
      </button>
      {/* <MyButt /> */}
    </>
  );
}

export default App;

interface Todo {
  id: number;
  name: string;
}

interface ListItemProps {
  item: Todo;
  onDelete: (id: number) => void;
}

function ListItem({ item, onDelete }: ListItemProps) {
  return (
    <>
      <li key={item.id}>
        {item.name}
        <button onClick={() => onDelete(item.id)}>Done</button>
      </li>
    </>
  );
}

function MyButt() {
  return (
    <>
      <span> my milkshake </span>
    </>
  );
}
