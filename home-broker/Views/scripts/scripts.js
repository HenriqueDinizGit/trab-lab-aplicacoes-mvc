const brokerEndpoint = "http://localhost:8080/task/user";

function hideLoader() {
  document.getElementById("loading").style.display = "none";
}

function show(acoes) {
  let tab = `<thead>
            <th scope="col">#</th>
            <th scope="col">Description</th>
        </thead>`;

  for (let acao of acoes) {
    tab += `
            <tr>
                <td scope="row">${acao.id}</td>
                <td>${acao.description}</td>
            </tr>
        `;
  }

  document.getElementById("acao").innerHTML = tab;
}

async function getAcoes() {
  let key = "Authorization";
  const response = await fetch(brokerEndpoint, {
    method: "GET",
    headers: new Headers({
      Authorization: localStorage.getItem(key),
    }),
  });

  var data = await response.json();
  console.log(data);
  if (response) hideLoader();
  show(data);
}

document.addEventListener("DOMContentLoaded", function (event) {
  if (!localStorage.getItem("Authorization"))
    window.location = "/view/login.html";
});

getTasks();