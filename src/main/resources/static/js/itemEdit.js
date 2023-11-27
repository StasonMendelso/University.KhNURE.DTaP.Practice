const previousButton = $("#previous");
const nextButton = $("#next");
disableNextButton();
enablePreviousButton();

requestMementoMetadata().then(handleMementoMetadata);
function requestMementoMetadata() {
    return new Promise(function (resolve) {
        $.ajax({
            type: "GET",
            dataType: "json",
            url: "/items/edit/state",
            success: function (mementoResponse) {
                resolve(mementoResponse);
            },
            error: handleError
        });
    });
}


previousButton.click(async function () {
    console.log("pressed previous");
    const mementoResponse = await getPreviousState();
    changeFields(mementoResponse.mementoState);
    handleMementoMetadata(mementoResponse.metadata);
})

async function getPreviousState() {
    let result;
    await $.ajax({
        type: "GET",
        dataType: "json",
        url: "/items/edit/state/previous",
        success: function (mementoResponse) {
            result = mementoResponse;
        },
        error: handleError
    })
    return result;
}

nextButton.click(async function () {
    console.log("pressed next");
    const mementoResponse = await getNextState();
    changeFields(mementoResponse.mementoState);
    handleMementoMetadata(mementoResponse.metadata);
})

async function getNextState() {
    let result;
    await $.ajax({
        type: "GET",
        dataType: "json",
        url: "/items/edit/state/next",
        success: function (mementoResponse) {
            result = mementoResponse;
        },
        error: handleError
    })
    return result;
}

const vendor = $("#vendor");
const name = $("#name");
const unit = $("#unit");
const weight = $("#weight");
const reserveRate = $("#reserveRate");
vendor.change(changeState);
name.change(changeState);
unit.change(changeState);
weight.change(changeState);
reserveRate.change(changeState);

function getJsonState() {
    const state = {
        id: $("#id").val(),
        vendor: vendor.val(),
        name: name.val(),
        unit: unit.val(),
        weight: weight.val(),
        reserveRate: reserveRate.val(),
    };
    return JSON.stringify(state);
}

function changeState() {
    console.log("state changed")
    const data = getJsonState();
    $.ajax({
        type: "POST",
        contentType: 'application/json',
        url: "/items/edit/state/save",
        data: data,
        success: function (mementoMetadata) {
            console.log("Responded")
            console.log(mementoMetadata);
            handleMementoMetadata(mementoMetadata);
        },
        error: handleError
    })
}

function handleError(jqXHR, exception) {
    if (jqXHR.status === 0) {
        alert('Not connect. Verify Network.');
    } else if (jqXHR.status == 404) {
        alert('Requested page not found (404).');
    } else if (jqXHR.status == 500) {
        alert('Internal Server Error (500).');
    } else if (exception === 'parsererror') {
        alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
        alert('Time out error.');
    } else if (exception === 'abort') {
        alert('Ajax request aborted.');
    } else {
        alert('Uncaught Error. ' + jqXHR.responseText);
    }

}


function disablePreviousButton() {
    previousButton.prop('disabled', true);
}

function enablePreviousButton() {
    previousButton.prop('disabled', false);
}

function disableNextButton() {
    nextButton.prop('disabled', true);
}

function enableNextButton() {
    nextButton.prop('disabled', false);
}

function handleMementoMetadata(mementoMetadata) {
    if (mementoMetadata.statesSize > 1) {
        enablePreviousButton();
    } else {
        disablePreviousButton();
    }
    if (mementoMetadata.currentIndex === mementoMetadata.statesSize - 1) {
        disableNextButton();
    } else {
        enableNextButton();
    }
    if (mementoMetadata.statesSize <= 1) {
        disableNextButton();
    }
    if (mementoMetadata.currentIndex === 0) {
        disablePreviousButton();
    }

}

function changeFields(state) {
    vendor.val(state.vendor);
    name.val(state.name);
    unit.val(state.unit);
    weight.val(state.weight);
    reserveRate.val(state.reserveRate);
}